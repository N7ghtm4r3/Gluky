package com.tecknobit.gluky.services.analyses.helpers;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.svg.converter.SvgConverter;
import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.gluky.services.analyses.helpers.ReportCreator.Translator.TranslatorKey;
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;
import kotlin.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.itextpdf.kernel.pdf.event.PdfDocumentEvent.END_PAGE;
import static com.itextpdf.kernel.pdf.event.PdfDocumentEvent.START_PAGE;
import static com.itextpdf.layout.borders.Border.NO_BORDER;
import static com.itextpdf.layout.properties.TextAlignment.*;
import static com.itextpdf.layout.properties.VerticalAlignment.MIDDLE;
import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager.RESOURCES_PATH;
import static com.tecknobit.gluky.services.analyses.helpers.ReportCreator.Translator.TranslatorKey.*;
import static com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem.UNSET_VALUE;
import static com.tecknobit.glukycore.ConstantsKt.*;

/**
 * The {@code ReportCreator} utility class is useful to create the reports pdfs with the given measurements. <br>
 * Some methods annotated as {@link Returner} in this context are intended like {@code @Composable} in {@code Compose Multiplatform},
 * so they represent a section of the pdfs and not properly a simple method and are defined in the javadocs as "{@code Component}"
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class ReportCreator {

    /**
     * {@code PRIMARY_COLOR} the constant value represents the primary color
     */
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(46, 191, 165);

    /**
     * {@code GREEN_COLOR} the constant value represents the custom green color
     */
    private static final DeviceRgb GREEN_COLOR = new DeviceRgb(76, 175, 80);

    /**
     * {@code YELLOW_COLOR} the constant value represents the custom yellow color
     */
    private static final DeviceRgb YELLOW_COLOR = new DeviceRgb(251, 192, 45);

    /**
     * {@code RED_COLOR} the constant value represents the custom red color
     */
    private static final DeviceRgb RED_COLOR = new DeviceRgb(229, 57, 53);

    /**
     * {@code PDF_SUFFIX} the constant value for the pdf suffix
     */
    private static final String PDF_SUFFIX = ".pdf";

    /**
     * {@code FREDOKA} the custom freedoka font
     */
    private static final String FREDOKA = "font/fredoka.ttf";

    /**
     * {@code COMICNEUE} the custom comicneue font
     */
    private static final String COMICNEUE = "font/comicneue.ttf";

    /**
     * {@code LOGO} the logo pathname
     */
    private static final String LOGO = "logo.png";

    /**
     * {@code LOGO_SIZE} the default size value applied to hte {@link #LOGO}
     */
    private static final float LOGO_SIZE = 65f;

    /**
     * {@code H1_SIZE} the size applied to the <strong>h1</strong> headers type
     */
    private static final float H1_SIZE = 22f;

    /**
     * {@code H1_SIZE} the size applied to the <strong>h2</strong> headers type
     */
    private static final float H2_SIZE = 18f;

    /**
     * {@code H3_SIZE} the size applied to the <strong>h3</strong> headers type
     */
    private static final float H3_SIZE = 14f;

    /**
     * {@code SUBTITLE_SIZE} the size applied to the subtitles text
     */
    private static final float SUBTITLE_SIZE = 11f;

    /**
     * {@code NOT_APPLICABLE_TEXT} the constant value used to represent a "Not Applicable" value
     */
    private static final String NOT_APPLICABLE_TEXT = "N/A";

    /**
     * {@code user} the user who requested the report creation
     */
    private final GlukyUser user;

    /**
     * {@code period} the period used to create the report
     */
    private final GlycemicTrendPeriod period;

    /**
     * {@code from} the start date from measurements have been retrieved
     */
    private final long from;

    /**
     * {@code to} the end date used to retrieve the measurements
     */
    private final long to;

    /**
     * {@code dailyMeasurements} the daily measurements retrieved
     */
    private final List<DailyMeasurements> dailyMeasurements;

    /**
     * {@code resourceUtils} utility used to get the file from resources folder
     */
    private final ResourcesUtils<Class<ReportCreator>> resourceUtils;

    /**
     * {@code reportPath} the path where save the report
     */
    private final String reportPath;

    /**
     * {@code pdfDocument} the pdf document helper
     */
    private final PdfDocument pdfDocument;

    /**
     * {@code document} the root element of the {@link #pdfDocument} used to handle the layout contents
     */
    private final Document document;

    /**
     * {@code locale} the locale of the {@link #user}
     */
    private final Locale locale;

    /**
     * {@code translator} utility class used to make the pdf reports internationalized
     */
    private final Translator translator;

    /**
     * {@code fredoka} the container element of the {@link #FREDOKA} font
     */
    private PdfFont fredoka;

    /**
     * {@code comicneue} the container element of the {@link #COMICNEUE} font
     */
    private PdfFont comicneue;

    /**
     * Constructor to init the creator
     *
     * @param user              The user who request the report creation
     * @param period            The period used to create the report
     * @param from              The start date from measurements have been retrieved
     * @param to                The end date used to retrieve the measurements
     * @param dailyMeasurements The daily measurements retrieved
     * @param reportId          The identifier of the report
     * @throws IOException when an error during the report creation occurred
     */
    public ReportCreator(GlukyUser user, GlycemicTrendPeriod period, long from, long to,
                         List<DailyMeasurements> dailyMeasurements, String reportId) throws IOException {
        this.user = user;
        this.period = period;
        this.from = from;
        this.to = to;
        this.dailyMeasurements = removeUnfilledMeasurements(dailyMeasurements);
        resourceUtils = new ResourcesUtils<>(ReportCreator.class);
        reportPath = REPORTS_KEY + "/" + reportId + PDF_SUFFIX;
        pdfDocument = new PdfDocument(new PdfWriter(RESOURCES_PATH + reportPath));
        document = new Document(pdfDocument);
        locale = Locale.forLanguageTag(user.getLanguage());
        translator = new Translator(locale);
        setTheme();
    }

    /**
     * Filter method used to remove the unfilled measurements from the list
     *
     * @param dailyMeasurements The daily measurements list to filer
     *
     * @return the list filtered as {@link List} of {@link DailyMeasurements}
     */
    private List<DailyMeasurements> removeUnfilledMeasurements(List<DailyMeasurements> dailyMeasurements) {
        return dailyMeasurements.stream().filter(DailyMeasurements::isFilled).toList();
    }

    /**
     * Method used to set the general theme of the pdf
     */
    private void setTheme() throws IOException {
        fredoka = loadFont(FREDOKA);
        comicneue = loadFont(COMICNEUE);
        pdfDocument.addEventHandler(START_PAGE, new Header(document));
        pdfDocument.addEventHandler(END_PAGE, new Footer(resourceUtils, document, comicneue, translator));
        document.setBottomMargin(65);
    }

    /**
     * Method used to load a font from a pathname
     *
     * @param font The pathname of the font to create
     *
     * @return the font loaded as {@link PdfFont}
     */
    private PdfFont loadFont(String font) throws IOException {
        byte[] fontBytes = resourceUtils.getResourceStream(font).readAllBytes();
        FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
        return PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI);
    }

    /**
     * Method used to create the pdf
     *
     * @return the report name and the report path as {@link Pair} of {@link String}
     *
     * @throws IOException when an error during the report creation occurred
     */
    public Pair<String, String> create() throws IOException {
        generateHeader();
        arrangeContent();
        pdfDocument.close();
        document.close();
        return new Pair<>(generateReportName(), reportPath);
    }

    /**
     * Method used to generate the name will have the report file
     *
     * @return the name of the report file as {@link String}
     */
    private String generateReportName() {
        TimeFormatter formatter = TimeFormatter.getInstance("dd-MM-yyyy");
        String reportName = translator.getI18NText(REPORT);
        String from = formatter.formatAsString(this.from);
        String to = formatter.formatAsString(this.to);
        return reportName + "_" + from + "_" + to + PDF_SUFFIX;
    }

    /**
     * Method used to generate the header of the pdf that will be in each page of the final document
     */
    private void generateHeader() throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.addCell(userCompleteName());
        table.addCell(logo());
        document.add(table);
        document.add(new LineSeparator(new SolidLine()));
        periodTitleSection();
    }

    /**
     * {@code Component} displays the complete name of the {@link #user}
     *
     * @return the component as {@link Cell}
     */
    @Returner
    private Cell userCompleteName() {
        Paragraph completeName = h1(user.getCompleteName())
                .simulateBold();
        ArrangerCell cell = new ArrangerCell(completeName);
        cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cell;
    }

    /**
     * {@code Component} displays the {@link #LOGO}
     *
     * @return the component as {@link Cell}
     */
    @Returner
    private Cell logo() throws IOException {
        ArrangerCell cell = new ArrangerCell(loadLogo());
        cell.setPaddingBottom(5f);
        return cell;
    }

    /**
     * Method used to load the logo to display
     *
     * @return the logo to display as {@link Image}
     */
    private Image loadLogo() throws IOException {
        ImageData logoData = ImageDataFactory.create(resourceUtils.getResourceStream(LOGO).readAllBytes());
        Image logo = new Image(logoData);
        logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        logo.setWidth(LOGO_SIZE);
        logo.setHeight(LOGO_SIZE);
        logo.setBorderRadius(new BorderRadius(10));
        return logo;
    }

    /**
     * {@code Component} displays the main title of the period selected to create the report
     */
    private void periodTitleSection() {
        Paragraph title = h2(translator.getI18NText(getPeriodTitleKey()))
                .setMultipliedLeading(0)
                .setPaddingTop(15);
        document.add(title);
        gapPeriod();
    }

    /**
     * Method used to get the i18n key of the title related to the {@link #period}
     *
     * @return the key to use as {@link TranslatorKey}
     */
    @Returner
    private TranslatorKey getPeriodTitleKey() {
        return switch (period) {
            case ONE_WEEK -> WEEKLY_REPORT;
            case ONE_MONTH -> MONTHLY_REPORT;
            case THREE_MONTHS -> THREE_MONTHS_REPORT;
            case FOUR_MONTHS -> FOUR_MONTHS_REPORT;
        };
    }

    /**
     * {@code Component} displays the period selected to create the report
     */
    private void gapPeriod() {
        TimeFormatter formatter = TimeFormatter.getInstance("dd/MM/yyyy");
        String from = formatter.formatAsString(this.from);
        String to = formatter.formatAsString(this.to);
        Paragraph gap = subtitle(from + " - " + to);
        document.add(gap);
    }

    /**
     * Main method used to arrange the content of the pdf in each page. <br>
     * This method create the table with the details about a {@link DailyMeasurements} and
     * arrange the related daily notes
     */
    private void arrangeContent() {
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE dd", locale);
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy", locale);
        HashSet<String> headersMonths = new HashSet<>();
        DailyMeasurements lastMeasurements = dailyMeasurements.get(dailyMeasurements.size() - 1);
        for (DailyMeasurements measurements : dailyMeasurements) {
            long creationDate = measurements.getCreationDate();
            String headerMonth = capitalize(monthFormatter.format(creationDate));
            if (!headersMonths.contains(headerMonth)) {
                document.add(h3(headerMonth));
                headersMonths.add(headerMonth);
            }
            document.add(dayIndicator(dayFormatter, creationDate));
            document.add(dailyRecord(measurements));
            String dailyNotes = measurements.getDailyNotes();
            if (!dailyNotes.isEmpty())
                attachDailyNotes(dailyNotes);
            if (measurements != lastMeasurements)
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }
    }

    /**
     * {@code Component} displays as indicator the day displayed in the current pdf page
     *
     * @param dayFormatter The formatter used to properly format the {@code creationDate}
     * @param creationDate The creation date of the daily measurements
     * @return the indicator as {@link Paragraph}
     */
    @Returner
    private Paragraph dayIndicator(SimpleDateFormat dayFormatter, long creationDate) {
        String day = capitalize(dayFormatter.format(creationDate));
        return new Paragraph(day)
                .setFont(comicneue)
                .setFontSize(14);
    }

    /**
     * Method used to capitalize a string which needs to be capital
     *
     * @param uncapitalizedString The string to capitalize
     * @return the string capitalized as {@link String}
     */
    private String capitalize(String uncapitalizedString) {
        String firstChar = String.valueOf(uncapitalizedString.charAt(0));
        return uncapitalizedString.replaceFirst(firstChar, firstChar.toUpperCase());
    }

    /**
     * {@code Component} displays table with the details about a daily measurements
     *
     * @param measurements The measurements to display in the table
     *
     * @return the table as {@link Table}
     */
    @Returner
    private Table dailyRecord(DailyMeasurements measurements) {
        Table table = new Table(6);
        table.useAllAvailableWidth();
        setHeaders(table);
        fillRow(table, measurements);
        table.setMarginBottom(10);
        return table;
    }

    /**
     * Method used to set the headers of the {@link #dailyRecord(DailyMeasurements)} table
     *
     * @param table The table where set the headers
     */
    private void setHeaders(Table table) {
        TranslatorKey[] headers = new TranslatorKey[]{MEASUREMENT, TIME, PRE_PRANDIAL, INSULIN_UNITS,
                POST_PRANDIAL, CONTENT};
        for (TranslatorKey key : headers) {
            Cell headerCell = new Cell();
            headerCell.add(new Paragraph(translator.getI18NText(key)));
            headerCell.setBackgroundColor(PRIMARY_COLOR);
            headerCell.setFontColor(ColorConstants.WHITE);
            headerCell.setTextAlignment(CENTER);
            headerCell.setFont(fredoka);
            table.addHeaderCell(headerCell);
        }
    }

    /**
     * Method used to fill the rows of the {@link #dailyRecord(DailyMeasurements)} table
     *
     * @param table The table to fill
     * @param measurements The measurements to display in the rows and in the table
     */
    private void fillRow(Table table, DailyMeasurements measurements) {
        for (MeasurementType type : MeasurementType.getEntries()) {
            table.addCell(measurementType(type));
            GlycemicMeasurementItem item = measurements.getMeasurement(type);
            boolean isMeal = item instanceof Meal;
            table.addCell(timeCell(item.getAnnotationDate()));
            table.addCell(prePrandial(item.getGlycemia()));
            table.addCell(insulinUnits(item.getInsulinUnits()));
            if (isMeal) {
                table.addCell(postPrandial(((Meal) item).getPostPrandialGlycemia()));
                table.addCell(mealContent(((Meal) item).getRawContent()));
            } else {
                table.addCell(notApplicabile());
                table.addCell(notApplicabile());
            }
        }
    }

    /**
     * {@code Component} displays the type of the measurement
     *
     * @param type The type of the measurement
     *
     * @return the measurement type text as {@link Cell}
     */
    @Returner
    private Cell measurementType(MeasurementType type) {
        return measurementCell(new Paragraph(getMeasurementTypeText(type)));
    }

    /**
     * Method used to get the translated i18n text for the related {@link MeasurementType}
     *
     * @param type The type from obtain the related text
     *
     * @return the translated text as {@link String}
     */
    @Returner
    private String getMeasurementTypeText(MeasurementType type) {
        return translator.getI18NText(
                switch (type) {
                    case BREAKFAST -> BREAKFAST;
                    case MORNING_SNACK -> MORNING_SNACK;
                    case LUNCH -> LUNCH;
                    case AFTERNOON_SNACK -> AFTERNOON_SNACK;
                    case DINNER -> DINNER;
                    case BASAL_INSULIN -> BASAL_INSULIN;
                }
        );
    }

    /**
     * {@code Component} displays the time when the measurement has been annotated
     *
     * @param annotationDate The date when the measurement has been annotated
     *
     * @return the time as {@link Cell}
     */
    @Returner
    private Cell timeCell(long annotationDate) {
        String cellText = "";
        if (annotationDate != UNSET_VALUE) {
            TimeFormatter timeFormatter = TimeFormatter.getInstance("HH:mm");
            cellText = timeFormatter.formatAsString(annotationDate);
        }
        return measurementCell(new Paragraph(cellText));
    }

    /**
     * {@code Component} displays the preprandial glycemia value
     *
     * @param prePrandialGlycemia The preprandial glycemia value
     *
     * @return the preprandial glycemia as {@link Cell}
     */
    @Wrapper
    @Returner
    private Cell prePrandial(int prePrandialGlycemia) {
        return glycemia(prePrandialGlycemia);
    }

    /**
     * {@code Component} displays the administered insulin units
     *
     * @param insulinUnits The administered insulin units
     *
     * @return the administered insulin units as {@link Cell}
     */
    @Returner
    private Cell insulinUnits(int insulinUnits) {
        return intValueCell(insulinUnits);
    }

    /**
     * {@code Component} displays the postprandial glycemia value
     *
     * @param postPrandialGlycemia The postprandial glycemia value
     *
     * @return the postprandial glycemia as {@link Cell}
     */
    @Wrapper
    @Returner
    private Cell postPrandial(int postPrandialGlycemia) {
        return glycemia(postPrandialGlycemia);
    }

    /**
     * {@code Component} displays a glycemia value
     *
     * @param glycemia The glycemia value
     *
     * @return a glycemia value as {@link Cell}
     */
    @Returner
    private Cell glycemia(int glycemia) {
        Color fontColor = ColorConstants.BLACK;
        DeviceRgb backgroundColor = glycemiaLevelBackground(glycemia);
        if (backgroundColor == RED_COLOR)
            fontColor = ColorConstants.WHITE;
        return intValueCell(glycemia)
                .setBackgroundColor(backgroundColor)
                .setFontColor(fontColor);
    }

    /**
     * Method used to get the color to apply based on the current {@code glycemia} value
     *
     * @param glycemia The value of the glycemia
     *
     * @return the color to apply to the background as {@link DeviceRgb}
     */
    @Returner
    private DeviceRgb glycemiaLevelBackground(int glycemia) {
        if (glycemia < 0)
            return (DeviceRgb) DeviceRgb.WHITE;
        if (glycemia < NORMAL_GLYCEMIA)
            return RED_COLOR;
        else if (glycemia < MEDIUM_HIGH_GLYCEMIA)
            return GREEN_COLOR;
        else if (glycemia < HYPER_GLYCEMIA)
            return YELLOW_COLOR;
        else
            return RED_COLOR;
    }

    /**
     * {@code Component} displays a integer value
     *
     * @param value The integer value to display
     *
     * @return integer value as {@link Cell}
     */
    @Returner
    private Cell intValueCell(int value) {
        String valueStringed = "";
        if (value != UNSET_VALUE)
            valueStringed = String.valueOf(value);
        return measurementCell(new Paragraph(valueStringed));
    }

    /**
     * {@code Component} displays the content of the meal
     *
     * @param content The content of the meal to display
     *
     * @return the content of the meal as {@link Cell}
     */
    @Returner
    private Cell mealContent(String content) {
        com.itextpdf.layout.element.List list = formatMealContent(content);
        return measurementCell(list).setTextAlignment(LEFT);
    }

    /**
     * Method used to format a raw content into displayable component
     *
     * @param content the raw content to format
     * @return the formatted raw content as {@link com.itextpdf.layout.element.List}
     */
    @Returner
    private com.itextpdf.layout.element.List formatMealContent(String content) {
        JSONObject jContent = new JSONObject(content);
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
        for (String mealKey : jContent.keySet()) {
            String mealQuantity = "(" + jContent.get(mealKey) + ")";
            list.add(new ListItem(mealKey + " " + mealQuantity));
        }
        return list;
    }

    /**
     * {@code Component} displays a {@link #NOT_APPLICABLE_TEXT}
     *
     * @return a {@link #NOT_APPLICABLE_TEXT} as {@link Cell}
     */
    @Returner
    private Cell notApplicabile() {
        return measurementCell(new Paragraph(NOT_APPLICABLE_TEXT));
    }

    /**
     * {@code Component} arrange the content in a custom cell inside the {@link #dailyRecord(DailyMeasurements)} table
     *
     * @param content The content to display
     *
     * @return the arranged cell as {@link Cell}
     */
    @Returner
    private Cell measurementCell(IElement content) {
        return new ArrangerCell(content, new SolidBorder(0.5f))
                .setFont(comicneue)
                .setTextAlignment(CENTER)
                .setVerticalAlignment(MIDDLE);
    }

    /**
     * {@code Component} displays the daily notes related to a {@link DailyMeasurements}
     *
     * @param dailyNotes The content of the daily notes
     */
    @Returner
    private void attachDailyNotes(String dailyNotes) {
        Paragraph dailyNotesTitle = new Paragraph(translator.getI18NText(DAILY_NOTES))
                .setFont(comicneue)
                .setFontSize(12)
                .simulateBold();
        document.add(dailyNotesTitle);
        Paragraph dailyNotesText = new Paragraph(dailyNotes)
                .setFont(comicneue)
                .setFontSize(12)
                .setTextAlignment(JUSTIFIED);
        document.add(dailyNotesText);
    }

    /**
     * {@code Component} displays a text formatted with the {@link #H1_SIZE}
     *
     * @param text The text of the header
     *
     * @return the <strong>h1</strong> header as {@link Paragraph}
     */
    @Returner
    private Paragraph h1(String text) {
        return header(text, H1_SIZE);
    }

    /**
     * {@code Component} displays a text formatted with the {@link #H2_SIZE}
     *
     * @param text The text of the header
     *
     * @return the <strong>h2</strong> header as {@link Paragraph}
     */
    @Returner
    private Paragraph h2(String text) {
        return header(text, H2_SIZE);
    }

    /**
     * {@code Component} displays a text formatted with the {@link #H3_SIZE}
     *
     * @param text The text of the header
     *
     * @return the <strong>h3/strong> header as {@link Paragraph}
     */
    @Returner
    private Paragraph h3(String text) {
        return header(text, H3_SIZE);
    }

    /**
     * {@code Component} displays a text formatted with a header size value
     *
     * @param text The text of the header
     * @param size The size to apply to the header
     *
     * @return the header as {@link Paragraph}
     */
    @Returner
    private Paragraph header(String text, float size) {
        return new Paragraph(text)
                .setFont(fredoka)
                .setFontSize(size);
    }

    /**
     * {@code Component} displays a text formatted with the {@link #SUBTITLE_SIZE}
     *
     * @param text The text of the subtitle
     *
     * @return the header as {@link Paragraph}
     */
    @Returner
    private Paragraph subtitle(String text) {
        return new Paragraph(text)
                .setFont(comicneue)
                .setFontSize(SUBTITLE_SIZE)
                .setFontColor(ColorConstants.GRAY);
    }

    /**
     * The {@code Header} utility class is used to arrange the header to apply to the pdf pages
     *
     * @author N7ghtm4r3 - Tecknobit
     *
     * @see AbstractPdfDocumentEventHandler
     */
    private static class Header extends AbstractPdfDocumentEventHandler {

        /**
         * {@code document} the root element of the {@link #pdfDocument} used to handle the layout contents
         */
        private final Document document;

        /**
         * Constructor to init the header
         * @param document The root element of the {@link #pdfDocument} used to handle the layout contents
         */
        private Header(Document document) {
            this.document = document;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfPage page = ((PdfDocumentEvent) event).getPage();
            PdfDocument pdfDocument = event.getDocument();
            if (pdfDocument.getPageNumber(page) > 1)
                document.setTopMargin(70);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument);
            float startX = pageSize.getLeft();
            float startY = pageSize.getTop();
            float waveWidth = pageSize.getWidth() * 0.7f;
            float waveHeight = 70f;
            canvas.saveState();
            canvas.setFillColor(PRIMARY_COLOR);
            canvas.moveTo(startX, startY);
            canvas.lineTo(startX + waveWidth, startY);
            canvas.curveTo(
                    startX + waveWidth * 0.85f, startY,
                    startX + waveWidth * 0.65f, startY - waveHeight,
                    startX + waveWidth * 0.5f, startY - waveHeight
            );
            canvas.curveTo(
                    startX + waveWidth * 0.35f, startY - waveHeight,
                    startX + waveWidth * 0.20f, startY,
                    startX, startY - waveHeight
            );
            canvas.lineTo(startX, startY);
            canvas.fill();
            canvas.restoreState();
        }

    }

    /**
     * The {@code Footer} utility class is used to arrange the footer to apply to the pdf pages
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see AbstractPdfDocumentEventHandler
     */
    private static class Footer extends AbstractPdfDocumentEventHandler {

        /**
         * {@code PLAYSTORE_ICON} the pathname for the Play Store's icon
         */
        private static final String PLAYSTORE_ICON = "playstore.svg";

        /**
         * {@code PLAYSTORE_URL} the url of the Play Store
         */
        private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.tecknobit.gluky";

        /**
         * {@code APPSTORE_ICON} the pathname for the App Store's icon
         */
        private static final String APPSTORE_ICON = "appstore.svg";

        /**
         * {@code APPSTORE_URL} the url of the App Store
         */
        private static final String APPSTORE_URL = "https://apps.apple.com/it/app/gluky"; // TODO: 30/05/2025 TO SET

        /**
         * {@code GITHUB_ICON} the pathname for the GitHub's icon
         */
        private static final String GITHUB_ICON = "github.svg";

        /**
         * {@code GITHUB_URL} the url of the GitHub repository
         */
        private static final String GITHUB_URL = "https://github.com/N7ghtm4r3/Gluky-Clients";

        /**
         * {@code ICON_SIZE} the constant size for the icons
         */
        private static final float ICON_SIZE = 17f;

        /**
         * {@code resourceUtils} utility used to get the file from resources folder
         */
        private final ResourcesUtils<Class<ReportCreator>> resourcesUtils;

        /**
         * {@code document} the root element of the {@link #pdfDocument} used to handle the layout contents
         */
        private final Document document;

        /**
         * {@code comicneue} the container element of the {@link #COMICNEUE} font
         */
        private final PdfFont comicneue;

        /**
         * {@code currentPageNumber} the number of the current page where the footer is placed
         */
        private int currentPageNumber;

        /**
         * {@code translator} utility class used to make the pdf reports internationalized
         */
        private final Translator translator;

        /**
         * Constructor to init the footer
         *
         * @param resourcesUtils The utility used to get the file from resources folder
         * @param document       The root element of the {@link #pdfDocument} used to handle the layout contents
         * @param comicneue      The container element of the {@link #COMICNEUE} font
         * @param translator     The utility class used to make the pdf reports internationalized
         */
        private Footer(ResourcesUtils<Class<ReportCreator>> resourcesUtils, Document document, PdfFont comicneue,
                       Translator translator) {
            this.resourcesUtils = resourcesUtils;
            this.document = document;
            this.comicneue = comicneue;
            this.translator = translator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfDocument pdfDocument = event.getDocument();
            PdfPage page = ((PdfDocumentEvent) event).getPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument);
            currentPageNumber = pdfDocument.getPageNumber(page);
            Rectangle backgroundBanner = createBackgroundBanner(pdfCanvas, page);
            try {
                addBannerContent(pdfDocument, page, backgroundBanner);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                pdfCanvas.release();
            }
        }

        /**
         * Method used to create the background banner element
         *
         * @param pdfCanvas The canvas of the pdf used to create the banner
         * @param page The page of the pdf
         *
         * @return the background banner element as {@link Rectangle}
         */
        private Rectangle createBackgroundBanner(PdfCanvas pdfCanvas, PdfPage page) {
            Rectangle backgroundBanner = createFooterBanner(page.getPageSize());
            pdfCanvas.setFillColor(PRIMARY_COLOR)
                    .rectangle(backgroundBanner)
                    .fill()
                    .stroke();
            return backgroundBanner;
        }

        /**
         * Method used to create the footer banner container
         *
         * @param pageSize The root element used to place the footer banner
         *
         * @return the footer banner container as {@link Rectangle}
         */
        @Returner
        private Rectangle createFooterBanner(Rectangle pageSize) {
            return new Rectangle(0, pageSize.getBottom(), pageSize.getWidth(), 65);
        }

        /**
         * Method used to add the content top of the footer banner
         *
         * @param pdfDocument The pdf document
         * @param page The page of the pdf
         * @param bannerContainer The container of the banner
         */
        private void addBannerContent(PdfDocument pdfDocument, PdfPage page, Rectangle bannerContainer) throws IOException {
            Canvas canvas = new Canvas(page, getBannerRootArea(bannerContainer));
            Table table = new Table(UnitValue.createPercentArray(new float[]{0.3f, 0.3f, 0.3f, 1.5f, 1.5f}));
            table.useAllAvailableWidth();
            table.addCell(playStoreIcon(pdfDocument));
            table.addCell(appStoreIcon(pdfDocument));
            table.addCell(githubIcon(pdfDocument));
            table.addCell(pageCount());
            table.addCell(generatedWithGluky());
            canvas.add(table);
            canvas.close();
        }

        /**
         * Method used to compute the root area of the banner container
         *
         * @param bannerContainer The banner container
         *
         * @return the root area of the banner container as {@link Rectangle}
         */
        @Returner
        private Rectangle getBannerRootArea(Rectangle bannerContainer) {
            float bannerHeight = bannerContainer.getHeight();
            float middleY = -(bannerHeight / 2) + 10;
            return new Rectangle(document.getLeftMargin(), middleY, bannerContainer.getWidth(), bannerHeight);
        }

        /**
         * {@code Component} displays the {@link #PLAYSTORE_ICON}
         *
         * @param pdfDocument The pdf document
         *
         * @return the {@link #PLAYSTORE_ICON} as {@link Cell}
         */
        @Wrapper
        @Returner
        private Cell playStoreIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, PLAYSTORE_ICON, PLAYSTORE_URL);
        }

        /**
         * {@code Component} displays the {@link #APPSTORE_ICON}
         *
         * @param pdfDocument The pdf document
         *
         * @return the {@link #APPSTORE_ICON} as {@link Cell}
         */
        @Wrapper
        @Returner
        private Cell appStoreIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, APPSTORE_ICON, APPSTORE_URL);
        }

        /**
         * {@code Component} displays the {@link #GITHUB_ICON}
         *
         * @param pdfDocument The pdf document
         *
         * @return the {@link #GITHUB_ICON} as {@link Cell}
         */
        @Wrapper
        @Returner
        private Cell githubIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, GITHUB_ICON, GITHUB_URL);
        }

        /**
         * {@code Component} displays a clickable icon
         *
         * @param pdfDocument The pdf document
         * @param icon The icon pathname
         * @param url The url to open on icon click
         *
         * @return the clickable icon as {@link Cell}
         */
        @Returner
        private Cell iconCell(PdfDocument pdfDocument, String icon, String url) throws IOException {
            return new ArrangerCell(iconData(pdfDocument, icon, url));
        }

        /**
         * Method used to load the icon data
         * @param pdfDocument The pdf document
         * @param icon The icon pathname
         * @param url The url to open on icon click
         *
         * @return the icon data as {@link Image}
         */
        private Image iconData(PdfDocument pdfDocument, String icon, String url) throws IOException {
            InputStream svgInput = resourcesUtils.getResourceStream(icon);
            Image imageIcon = SvgConverter.convertToImage(svgInput, pdfDocument);
            imageIcon.setAction(PdfAction.createURI(url));
            imageIcon.setWidth(ICON_SIZE);
            imageIcon.setWidth(ICON_SIZE);
            return imageIcon;
        }

        /**
         * {@code Component} displays the current page count
         *
         * @return the current page count as {@link Cell}
         */
        @Returner
        private Cell pageCount() {
            Paragraph pageCount = new Paragraph(translator.getI18NText(PAGE) + " " + currentPageNumber)
                    .setFont(comicneue)
                    .setFontColor(ColorConstants.WHITE);
            return new ArrangerCell(pageCount).setTextAlignment(CENTER);
        }

        /**
         * {@code Component} displays the {@code generated by} text
         *
         * @return the {@code generated by} text as {@link Cell}
         */
        @Returner
        private Cell generatedWithGluky() {
            Paragraph pageCount = new Paragraph(translator.getI18NText(GENERATED_WITH_GLUKY))
                    .setFont(comicneue)
                    .setFontColor(ColorConstants.WHITE);
            return new ArrangerCell(pageCount).setTextAlignment(CENTER);
        }

    }

    /**
     * The {@code ArrangerCell} custom cell used to arrange the content following the same rules such theming, layout and
     * styling for all the document
     *
     * @author N7ghtm4r3 - Tecknobit
     *
     * @see Cell
     */
    private static class ArrangerCell extends Cell {

        /**
         * Constructor to init the cell
         *
         * @param content The content of the cell
         */
        public ArrangerCell(IElement content) {
            this(content, NO_BORDER);
        }

        /**
         * Constructor to init the cell
         *
         * @param content The content of the cell
         * @param border The border style to apply to the cell
         */
        public ArrangerCell(IElement content, Border border) {
            setBorder(border);
            arrange(content);
        }

        /**
         * Method used to arrange the content invoking the correct method based on the type of the content to display
         *
         * @param content The content to display in the cell
         */
        private void arrange(IElement content) {
            if (content instanceof Image)
                add((Image) content);
            else if (content instanceof IBlockElement)
                add((IBlockElement) content);
        }

    }

    /**
     * The {@code Translator} utility class used to internationalize the report based on the locale of the user who
     * request the report creation
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    static class Translator {

        /**
         * The {@code TranslatorKey} represents an available international resource to use in the report
         *
         * @param keyValue The value of the key
         *
         * @author N7ghtm4r3 - Tecknobit
         */
        record TranslatorKey(String keyValue) {

            /**
             * {@code WEEKLY_REPORT} constant value for the {@code weekly_report} resource
             */
            static final TranslatorKey WEEKLY_REPORT = new TranslatorKey("weekly_report");

            /**
             * {@code MONTHLY_REPORT} constant value for the {@code monthly_report} resource
             */
            static final TranslatorKey MONTHLY_REPORT = new TranslatorKey("monthly_report");

            /**
             * {@code THREE_MONTHS_REPORT} constant value for the {@code three_months_report} resource
             */
            static final TranslatorKey THREE_MONTHS_REPORT = new TranslatorKey("three_months_report");

            /**
             * {@code FOUR_MONTHS_REPORT} constant value for the {@code four_months_report} resource
             */
            static final TranslatorKey FOUR_MONTHS_REPORT = new TranslatorKey("four_months_report");

            /**
             * {@code PAGE} constant value for the {@code page} resource
             */
            static final TranslatorKey PAGE = new TranslatorKey("page");

            /**
             * {@code GENERATED_WITH_GLUKY} constant value for the {@code generated_with_gluky} resource
             */
            static final TranslatorKey GENERATED_WITH_GLUKY = new TranslatorKey("generated_with_gluky");

            /**
             * {@code MEASUREMENT} constant value for the {@code measurement} resource
             */
            static final TranslatorKey MEASUREMENT = new TranslatorKey("measurement");

            /**
             * {@code TIME} constant value for the {@code time} resource
             */
            static final TranslatorKey TIME = new TranslatorKey("time");

            /**
             * {@code PRE_PRANDIAL} constant value for the {@code pre-prandial} resource
             */
            static final TranslatorKey PRE_PRANDIAL = new TranslatorKey("pre-prandial");

            /**
             * {@code POST_PRANDIAL} constant value for the {@code post-prandial} resource
             */
            static final TranslatorKey POST_PRANDIAL = new TranslatorKey("post-prandial");

            /**
             * {@code INSULIN_UNITS} constant value for the {@code insulin_units} resource
             */
            static final TranslatorKey INSULIN_UNITS = new TranslatorKey("insulin_units");

            /**
             * {@code CONTENT} constant value for the {@code content} resource
             */
            static final TranslatorKey CONTENT = new TranslatorKey("content");

            /**
             * {@code BREAKFAST} constant value for the {@code breakfast} resource
             */
            static final TranslatorKey BREAKFAST = new TranslatorKey("breakfast");

            /**
             * {@code MORNING_SNACK} constant value for the {@code morning_snack} resource
             */
            static final TranslatorKey MORNING_SNACK = new TranslatorKey("morning_snack");

            /**
             * {@code LUNCH} constant value for the {@code lunch} resource
             */
            static final TranslatorKey LUNCH = new TranslatorKey("lunch");

            /**
             * {@code AFTERNOON_SNACK} constant value for the {@code afternoon_snack} resource
             */
            static final TranslatorKey AFTERNOON_SNACK = new TranslatorKey("afternoon_snack");

            /**
             * {@code DINNER} constant value for the {@code dinner} resource
             */
            static final TranslatorKey DINNER = new TranslatorKey("dinner");

            /**
             * {@code BASAL_INSULIN} constant value for the {@code basal_insulin} resource
             */
            static final TranslatorKey BASAL_INSULIN = new TranslatorKey("basal_insulin");

            /**
             * {@code DAILY_NOTES} constant value for the {@code daily_notes} resource
             */
            static final TranslatorKey DAILY_NOTES = new TranslatorKey("daily_notes");

            /**
             * {@code REPORT} constant value for the {@code report} resource
             */
            static final TranslatorKey REPORT = new TranslatorKey("report");

        }

        /**
         * {@code REPORT_MESSAGES} the pathname where the resources are located
         */
        private static final String REPORT_MESSAGES = "lang/report_messages";

        /**
         * {@code resources} the bundles manager used to retrieve the resources messages
         */
        private final ResourceBundle resources;

        /**
         * Constructor to init the translator
         *
         * @param locale The locale of the user who request the report creation
         */
        public Translator(Locale locale) {
            resources = ResourceBundle.getBundle(REPORT_MESSAGES, locale);
        }

        /**
         * Method used to retrieve the i18n resource
         *
         * @param key The key of the resource to retrieve
         *
         * @return the i18n resource as {@link String}
         */
        public String getI18NText(TranslatorKey key) {
            return resources.getString(key.keyValue());
        }

    }

}
