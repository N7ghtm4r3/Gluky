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
import com.tecknobit.gluky.services.measurements.entities.DailyMeasurements;
import com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem;
import com.tecknobit.gluky.services.measurements.entities.types.Meal;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;
import com.tecknobit.glukycore.enums.MeasurementType;
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
import static com.tecknobit.gluky.services.analyses.helpers.ReportGenerator.Translator.TranslatorKey.*;
import static com.tecknobit.gluky.services.measurements.entities.types.GlycemicMeasurementItem.UNSET_VALUE;
import static com.tecknobit.glukycore.ConstantsKt.*;

public class ReportGenerator {

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(46, 191, 165);

    private static final DeviceRgb GREEN_COLOR = new DeviceRgb(76, 175, 80);

    private static final DeviceRgb YELLOW_COLOR = new DeviceRgb(251, 192, 45);

    private static final DeviceRgb RED_COLOR = new DeviceRgb(229, 57, 53);

    private static final String FREDOKA = "font/fredoka.ttf";

    private static final String COMICNEUE = "font/comicneue.ttf";

    private static final String LOGO = "logo.png";

    private static final float LOGO_SIZE = 65f;

    private static final float H1_SIZE = 22f;

    private static final float H2_SIZE = 18f;

    private static final float H3_SIZE = 14f;

    private static final float SUBTITLE_SIZE = 11f;

    private static final String NOT_APPLICABLE_TEXT = "N/A";

    private final GlukyUser user;

    private final GlycemicTrendPeriod period;

    private final long from;

    private final long to;

    private final List<DailyMeasurements> dailyMeasurements;

    private final ResourcesUtils<Class<ReportGenerator>> resourceUtils;

    private final String reportPath;

    private final PdfDocument pdfDocument;

    private final Document document;

    private final Locale locale;

    private final Translator translator;

    private PdfFont fredoka;

    private PdfFont comicneue;

    public ReportGenerator(GlukyUser user, GlycemicTrendPeriod period, long from, long to,
                           List<DailyMeasurements> dailyMeasurements, String reportId) throws IOException {
        this.user = user;
        this.period = period;
        this.from = from;
        this.to = to;
        this.dailyMeasurements = dailyMeasurements;
        resourceUtils = new ResourcesUtils<>(ReportGenerator.class);
        reportPath = REPORTS_KEY + "/" + reportId + ".pdf";
        pdfDocument = new PdfDocument(new PdfWriter(RESOURCES_PATH + reportPath));
        document = new Document(pdfDocument);
        locale = Locale.forLanguageTag(user.getLanguage());
        translator = new Translator(locale);
        setTheme();
    }

    private void setTheme() throws IOException {
        fredoka = loadFont(FREDOKA);
        comicneue = loadFont(COMICNEUE);
        pdfDocument.addEventHandler(START_PAGE, new Header(document));
        pdfDocument.addEventHandler(END_PAGE, new Footer(resourceUtils, document, comicneue, translator));
        document.setBottomMargin(65);
    }

    private PdfFont loadFont(String font) throws IOException {
        byte[] fontBytes = resourceUtils.getResourceStream(font).readAllBytes();
        FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
        return PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI);
    }

    public String generate() throws IOException {
        generateHeader();
        arrangeContent();
        pdfDocument.close();
        document.close();
        return reportPath;
    }

    private void generateHeader() throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.addCell(userCompleteName());
        table.addCell(logo());
        document.add(table);
        document.add(new LineSeparator(new SolidLine()));
        periodTitleSection();
    }

    @Returner
    private Cell userCompleteName() {
        Paragraph completeName = h1(user.getCompleteName())
                .simulateBold();
        ArrangerCell cell = new ArrangerCell(completeName);
        cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cell;
    }

    @Returner
    private Cell logo() throws IOException {
        ArrangerCell cell = new ArrangerCell(loadLogo());
        cell.setPaddingBottom(5f);
        return cell;
    }

    private Image loadLogo() throws IOException {
        ImageData logoData = ImageDataFactory.create(resourceUtils.getResourceStream(LOGO).readAllBytes());
        Image logo = new Image(logoData);
        logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        logo.setWidth(LOGO_SIZE);
        logo.setHeight(LOGO_SIZE);
        logo.setBorderRadius(new BorderRadius(10));
        return logo;
    }

    private void periodTitleSection() {
        Paragraph title = h2(translator.getI18NText(getPeriodTitleKey()))
                .setMultipliedLeading(0)
                .setPaddingTop(15);
        document.add(title);
        gapPeriod();
    }

    private void gapPeriod() {
        TimeFormatter formatter = TimeFormatter.getInstance("dd/MM/yyyy");
        String from = formatter.formatAsString(this.from);
        String to = formatter.formatAsString(this.to);
        Paragraph gap = subtitle(from + " - " + to);
        document.add(gap);
    }

    private Translator.TranslatorKey getPeriodTitleKey() {
        return switch (period) {
            case ONE_WEEK -> WEEKLY_REPORT;
            case ONE_MONTH -> MONTHLY_REPORT;
            case THREE_MONTHS -> THREE_MONTHS_REPORT;
            case FOUR_MONTHS -> FOUR_MONTHS_REPORT;
        };
    }

    private void arrangeContent() {
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE dd", locale);
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy", locale);
        HashSet<String> headersMonths = new HashSet<>();
        DailyMeasurements lastMeasurements = dailyMeasurements.get(dailyMeasurements.size() - 1);
        for (DailyMeasurements measurements : dailyMeasurements) {
            // TODO: 02/06/2025 DOES NOT PRINT ANY IF THE measurements HAS NO DATA 
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

    @Returner
    private Table dailyRecord(DailyMeasurements measurements) {
        Table table = new Table(6);
        table.useAllAvailableWidth();
        setHeaders(table);
        fillRow(table, measurements);
        table.setMarginBottom(10);
        return table;
    }

    private void setHeaders(Table table) {
        Translator.TranslatorKey[] headers = new Translator.TranslatorKey[]{MEASUREMENT, TIME, PRE_PRANDIAL, INSULIN_UNITS,
                POST_PRANDIAL, CONTENT};
        for (Translator.TranslatorKey key : headers) {
            Cell headerCell = new Cell();
            headerCell.add(new Paragraph(translator.getI18NText(key)));
            headerCell.setBackgroundColor(PRIMARY_COLOR);
            headerCell.setFontColor(ColorConstants.WHITE);
            headerCell.setTextAlignment(CENTER);
            headerCell.setFont(fredoka);
            table.addHeaderCell(headerCell);
        }
    }

    @Returner
    private Paragraph dayIndicator(SimpleDateFormat dayFormatter, long creationDate) {
        String day = capitalize(dayFormatter.format(creationDate));
        return new Paragraph(day)
                .setFont(comicneue)
                .setFontSize(14);
    }

    private String capitalize(String uncapitalizedString) {
        String firstChar = String.valueOf(uncapitalizedString.charAt(0));
        return uncapitalizedString.replaceFirst(firstChar, firstChar.toUpperCase());
    }

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

    @Returner
    private Cell measurementType(MeasurementType type) {
        return measurementCell(new Paragraph(getMeasurementTypeText(type)));
    }

    @Returner
    private String getMeasurementTypeText(MeasurementType type) {
        return translator.getI18NText(switch (type) {
            case BREAKFAST -> BREAKFAST;
            case MORNING_SNACK -> MORNING_SNACK;
            case LUNCH -> LUNCH;
            case AFTERNOON_SNACK -> AFTERNOON_SNACK;
            case DINNER -> DINNER;
            case BASAL_INSULIN -> BASAL_INSULIN;
        });
    }

    @Returner
    private Cell timeCell(long annotationDate) {
        String cellText = "";
        if (annotationDate != UNSET_VALUE) {
            TimeFormatter timeFormatter = TimeFormatter.getInstance("HH:mm");
            cellText = timeFormatter.formatAsString(annotationDate);
        }
        return measurementCell(new Paragraph(cellText));
    }

    @Wrapper
    @Returner
    private Cell prePrandial(int prePrandialGlycemia) {
        return glycemia(prePrandialGlycemia);
    }

    @Returner
    private Cell insulinUnits(int insulinUnits) {
        return intValueCell(insulinUnits);
    }

    @Wrapper
    @Returner
    private Cell postPrandial(int postPrandialGlycemia) {
        return glycemia(postPrandialGlycemia);
    }

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

    @Returner
    private Cell intValueCell(int value) {
        String valueStringed = "";
        if (value != UNSET_VALUE)
            valueStringed = String.valueOf(value);
        return measurementCell(new Paragraph(valueStringed));
    }

    @Returner
    private Cell mealContent(String content) {
        JSONObject jContent = new JSONObject(content);
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
        for (String mealKey : jContent.keySet()) {
            String mealQuantity = "(" + jContent.get(mealKey) + ")";
            list.add(new ListItem(mealKey + " " + mealQuantity));
        }
        return measurementCell(list).setTextAlignment(LEFT);
    }

    @Returner
    private Cell notApplicabile() {
        return measurementCell(new Paragraph(NOT_APPLICABLE_TEXT));
    }

    @Returner
    private Cell measurementCell(IElement content) {
        return new ArrangerCell(content, new SolidBorder(0.5f))
                .setFont(comicneue)
                .setTextAlignment(CENTER)
                .setVerticalAlignment(MIDDLE);
    }

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

    @Returner
    private Paragraph h1(String text) {
        return header(text, H1_SIZE);
    }

    @Returner
    private Paragraph h2(String text) {
        return header(text, H2_SIZE);
    }

    @Returner
    private Paragraph h3(String text) {
        return header(text, H3_SIZE);
    }

    @Returner
    private Paragraph header(String text, float size) {
        return new Paragraph(text)
                .setFont(fredoka)
                .setFontSize(size);
    }

    @Returner
    private Paragraph subtitle(String text) {
        return new Paragraph(text)
                .setFont(comicneue)
                .setFontSize(SUBTITLE_SIZE)
                .setFontColor(ColorConstants.GRAY);
    }

    private static class Header extends AbstractPdfDocumentEventHandler {

        private final Document document;

        private Header(Document document) {
            this.document = document;
        }

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

    private static class Footer extends AbstractPdfDocumentEventHandler {

        private static final String PLAYSTORE_ICON = "playstore.svg";

        private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.tecknobit.gluky";

        private static final String APPSTORE_ICON = "appstore.svg";

        private static final String APPSTORE_URL = "https://apps.apple.com/it/app/gluky"; // TODO: 30/05/2025 TO SET

        private static final String GITHUB_ICON = "github.svg";

        private static final String GITHUB_URL = "https://github.com/N7ghtm4r3/Gluky-Clients";

        private static final float ICON_SIZE = 17f;

        private final ResourcesUtils<Class<ReportGenerator>> resourcesUtils;

        private final Document document;

        private final PdfFont comicneue;

        private int currentPageNumber;

        private final Translator translator;

        private Footer(ResourcesUtils<Class<ReportGenerator>> resourcesUtils, Document document, PdfFont comicneue,
                       Translator translator) {
            this.resourcesUtils = resourcesUtils;
            this.document = document;
            this.comicneue = comicneue;
            this.translator = translator;
        }

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

        private Rectangle createBackgroundBanner(PdfCanvas pdfCanvas, PdfPage page) {
            Rectangle backgroundBanner = createFooterBanner(page.getPageSize());
            pdfCanvas.setFillColor(PRIMARY_COLOR)
                    .rectangle(backgroundBanner)
                    .fill()
                    .stroke();
            return backgroundBanner;
        }

        @Returner
        private Rectangle createFooterBanner(Rectangle pageSize) {
            return new Rectangle(0, pageSize.getBottom(), pageSize.getWidth(), 65);
        }

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

        @Returner
        private Rectangle getBannerRootArea(Rectangle bannerContainer) {
            float bannerHeight = bannerContainer.getHeight();
            float middleY = -(bannerHeight / 2) + 10;
            return new Rectangle(document.getLeftMargin(), middleY, bannerContainer.getWidth(), bannerHeight);
        }

        @Wrapper
        private Cell playStoreIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, PLAYSTORE_ICON, PLAYSTORE_URL);
        }

        @Wrapper
        private Cell appStoreIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, APPSTORE_ICON, APPSTORE_URL);
        }

        @Wrapper
        private Cell githubIcon(PdfDocument pdfDocument) throws IOException {
            return iconCell(pdfDocument, GITHUB_ICON, GITHUB_URL);
        }

        @Returner
        private Cell iconCell(PdfDocument pdfDocument, String icon, String url) throws IOException {
            return new ArrangerCell(iconData(pdfDocument, icon, url));
        }

        private Image iconData(PdfDocument pdfDocument, String icon, String url) throws IOException {
            InputStream svgInput = resourcesUtils.getResourceStream(icon);
            Image imageIcon = SvgConverter.convertToImage(svgInput, pdfDocument);
            imageIcon.setAction(PdfAction.createURI(url));
            imageIcon.setWidth(ICON_SIZE);
            imageIcon.setWidth(ICON_SIZE);
            return imageIcon;
        }

        private Cell pageCount() {
            Paragraph pageCount = new Paragraph(translator.getI18NText(PAGE) + " " + currentPageNumber)
                    .setFont(comicneue)
                    .setFontColor(ColorConstants.WHITE);
            return new ArrangerCell(pageCount).setTextAlignment(CENTER);
        }

        private Cell generatedWithGluky() {
            Paragraph pageCount = new Paragraph(translator.getI18NText(GENERATED_WITH_GLUKY))
                    .setFont(comicneue)
                    .setFontColor(ColorConstants.WHITE);
            return new ArrangerCell(pageCount).setTextAlignment(CENTER);
        }

    }

    private static class ArrangerCell extends Cell {

        public ArrangerCell(IElement content) {
            this(content, NO_BORDER);
        }

        public ArrangerCell(IElement content, Border border) {
            setBorder(border);
            arrange(content);
        }

        private void arrange(IElement content) {
            if (content instanceof Image)
                add((Image) content);
            else if (content instanceof IBlockElement)
                add((IBlockElement) content);
        }

    }

    static class Translator {

        record TranslatorKey(String keyValue) {

            static final TranslatorKey WEEKLY_REPORT = new TranslatorKey("weekly_report");

            static final TranslatorKey MONTHLY_REPORT = new TranslatorKey("monthly_report");

            static final TranslatorKey THREE_MONTHS_REPORT = new TranslatorKey("three_months_report");

            static final TranslatorKey FOUR_MONTHS_REPORT = new TranslatorKey("four_months_report");

            static final TranslatorKey PAGE = new TranslatorKey("page");

            static final TranslatorKey GENERATED_WITH_GLUKY = new TranslatorKey("generated_with_gluky");

            static final TranslatorKey MEASUREMENT = new TranslatorKey("measurement");

            static final TranslatorKey TIME = new TranslatorKey("time");

            static final TranslatorKey PRE_PRANDIAL = new TranslatorKey("pre-prandial");

            static final TranslatorKey POST_PRANDIAL = new TranslatorKey("post-prandial");

            static final TranslatorKey INSULIN_UNITS = new TranslatorKey("insulin_units");

            static final TranslatorKey CONTENT = new TranslatorKey("content");

            static final TranslatorKey BREAKFAST = new TranslatorKey("breakfast");

            static final TranslatorKey MORNING_SNACK = new TranslatorKey("morning_snack");

            static final TranslatorKey LUNCH = new TranslatorKey("lunch");

            static final TranslatorKey AFTERNOON_SNACK = new TranslatorKey("afternoon_snack");

            static final TranslatorKey DINNER = new TranslatorKey("dinner");

            static final TranslatorKey BASAL_INSULIN = new TranslatorKey("basal_insulin");

            static final TranslatorKey DAILY_NOTES = new TranslatorKey("daily_notes");

        }

        private static final String REPORT_MESSAGES = "lang/report_messages";

        private final ResourceBundle resources;

        public Translator(Locale locale) {
            resources = ResourceBundle.getBundle(REPORT_MESSAGES, locale);
        }

        public String getI18NText(TranslatorKey key) {
            return resources.getString(key.keyValue());
        }

    }

}
