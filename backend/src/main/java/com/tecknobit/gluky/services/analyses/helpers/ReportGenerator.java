package com.tecknobit.gluky.services.analyses.helpers;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.equinoxcore.annotations.Returner;
import com.tecknobit.gluky.services.users.entity.GlukyUser;
import com.tecknobit.glukycore.enums.GlycemicTrendPeriod;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.itextpdf.kernel.geom.PageSize.A4;
import static com.tecknobit.gluky.services.analyses.helpers.ReportGenerator.Translator.TranslatorKey.*;

public class ReportGenerator {

    private static final ResourcesUtils<Class<ReportGenerator>> resourceUtils = new ResourcesUtils<>(ReportGenerator.class);

    private static final String FREDOKA = "font/fredoka.ttf";

    private static final String COMICNEUE = "font/comicneue.ttf";

    private static final float H1_SIZE = 22f;

    private static final float H2_SIZE = 18f;

    private final PdfDocument pdfDocument;

    private final GlukyUser user;

    private final Document document;

    private final Translator translator;

    private final GlycemicTrendPeriod period;

    private PdfFont comicneue;

    private PdfFont fredoka;

    public ReportGenerator(GlukyUser user, GlycemicTrendPeriod period, String reportPath) throws IOException {
        reportPath = "resources/reports/test.pdf";
        this.user = user;
        this.period = period;
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

    private void generateHeader() {
        document.add(h1(user.getCompleteName()));
        document.add(new LineSeparator(new SolidLine()));
        document.add(h2(translator.getTranslatedText(getPeriodTitleKey())));
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
                .setFontSize(size)
                .setMultipliedLeading(1f);
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

        public String getTranslatedText(TranslatorKey key) {
            return resources.getString(key.keyValue());
        }

    }

}
