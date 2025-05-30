package com.tecknobit.gluky.services.analyses.helpers;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.svg.converter.SvgConverter;
import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.itextpdf.kernel.pdf.event.PdfDocumentEvent.END_PAGE;
import static com.itextpdf.layout.borders.Border.NO_BORDER;
import static com.itextpdf.layout.properties.TextAlignment.CENTER;
import static com.tecknobit.gluky.services.analyses.helpers.ReportGenerator.Translator.TranslatorKey.*;

public class ReportGenerator {

    private static final TimeFormatter formatter = TimeFormatter.getInstance("dd/MM/yyyy");

    private static final String FREDOKA = "font/fredoka.ttf";

    private static final String COMICNEUE = "font/comicneue.ttf";

    // TODO: 30/05/2025 TO USE THE REAL LOGO
    private static final String LOGO = "logo.png";

    private static final float LOGO_SIZE = 65f;

    private static final float H1_SIZE = 22f;

    private static final float H2_SIZE = 18f;

    private static final float SUBTITLE_SIZE = 11f;

    private final GlukyUser user;

    private final GlycemicTrendPeriod period;

    private final long from;

    private final long to;

    private final ResourcesUtils<Class<ReportGenerator>> resourceUtils;

    private final PdfDocument pdfDocument;

    private final Document document;

    private final Translator translator;

    private PdfFont comicneue;

    private PdfFont fredoka;

    public ReportGenerator(GlukyUser user, GlycemicTrendPeriod period, long from, long to, String reportPath) throws IOException {
        reportPath = "resources/reports/test.pdf";
        this.user = user;
        this.period = period;
        this.from = from;
        this.to = to;
        resourceUtils = new ResourcesUtils<>(ReportGenerator.class);
        pdfDocument = new PdfDocument(new PdfWriter(reportPath));
        document = new Document(pdfDocument);
        translator = new Translator(user.getLanguage());
        setTheme();
    }

    private void setTheme() throws IOException {
        fredoka = loadFont(FREDOKA);
        comicneue = loadFont(COMICNEUE);
        pdfDocument.addEventHandler(END_PAGE, new Footer(resourceUtils, document, comicneue, translator));
    }

    private PdfFont loadFont(String font) throws IOException {
        byte[] fontBytes = resourceUtils.getResourceStream(font).readAllBytes();
        FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
        return PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI);
    }

    public PdfDocument generate() throws IOException {
        generateHeader();
        document.close();
        return pdfDocument;
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

    @Returner
    private Paragraph h1(String text) {
        return header(text, H1_SIZE);
    }

    @Returner
    private Paragraph h2(String text) {
        return header(text, H2_SIZE);
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
            pdfCanvas.setFillColor(new DeviceRgb(46, 191, 165))
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
            table.setWidth(UnitValue.createPercentValue(100));
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
            setBorder(NO_BORDER);
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

        }

        private static final String REPORT_MESSAGES = "lang/report_messages";

        private final ResourceBundle resources;

        public Translator(String language) {
            resources = ResourceBundle.getBundle(REPORT_MESSAGES, Locale.forLanguageTag(language));
        }

        public String getI18NText(TranslatorKey key) {
            return resources.getString(key.keyValue());
        }

    }

}
