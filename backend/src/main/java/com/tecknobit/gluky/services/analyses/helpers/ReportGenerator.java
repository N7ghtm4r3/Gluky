package com.tecknobit.gluky.services.analyses.helpers;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.itextpdf.kernel.geom.PageSize.A4;
import static com.tecknobit.gluky.services.analyses.helpers.ReportGenerator.Translator.TranslatorKey.*;

public class ReportGenerator {

    private static final TimeFormatter formatter = TimeFormatter.getInstance("dd/MM/yyyy");

    private static final String FREDOKA = "font/fredoka.ttf";

    private static final String COMICNEUE = "font/comicneue.ttf";

    private static final String LOGO = "logo.png";

    private static final float H1_SIZE = 22f;

    private static final float H2_SIZE = 18f;

    private static final float SUBTITLE_SIZE = 10f;

    private final ResourcesUtils<Class<ReportGenerator>> resourceUtils;

    private final PdfDocument pdfDocument;

    private final GlukyUser user;

    private final Document document;

    private final Translator translator;

    private final GlycemicTrendPeriod period;

    private final long from;

    private final long to;

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
        document = new Document(pdfDocument, A4);
        translator = new Translator(user.getLanguage());
        setTheme();
    }

    private void setTheme() throws IOException {
        fredoka = loadFont(FREDOKA);
        comicneue = loadFont(COMICNEUE);
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

    private Cell userCompleteName() {
        Cell cell = new Cell();
        cell.setBorder(null);
        Paragraph completeName = h1(user.getCompleteName())
                .simulateBold();
        cell.add(completeName);
        cell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cell;
    }

    // TODO: 30/05/2025 TO USE THE REAL LOGO 
    private Cell logo() throws IOException {
        Cell cell = new Cell();
        cell.setBorder(null);
        cell.add(loadLogo());
        return cell;
    }

    private Image loadLogo() throws IOException {
        ImageData logoData = ImageDataFactory.create(resourceUtils.getResourceStream(LOGO).readAllBytes());
        Image logo = new Image(logoData);
        logo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        logo.setWidth(75);
        logo.setHeight(75);
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
                .setFontColor(ColorConstants.LIGHT_GRAY);
    }

    static class Translator {

        record TranslatorKey(String keyValue) {

            static final TranslatorKey WEEKLY_REPORT = new TranslatorKey("weekly_report");

            static final TranslatorKey MONTHLY_REPORT = new TranslatorKey("monthly_report");

            static final TranslatorKey THREE_MONTHS_REPORT = new TranslatorKey("three_months_report");

            static final TranslatorKey FOUR_MONTHS_REPORT = new TranslatorKey("four_months_report");

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
