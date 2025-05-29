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

import java.io.IOException;

import static com.itextpdf.kernel.geom.PageSize.A4;

public class ReportGenerator {

    private static final ResourcesUtils<Class<ReportGenerator>> resourceUtils = new ResourcesUtils<>(ReportGenerator.class);

    private static final String FREDOKA = "font/fredoka.ttf";

    private static final String COMICNEUE = "font/comicneue.ttf";

    private static final float H1_SIZE = 22f;

    private static final float H2_SIZE = 18f;

    private final PdfDocument pdfDocument;

    private final GlukyUser user;

    private final Document document;

    private PdfFont comicneue;

    private PdfFont fredoka;

    public ReportGenerator(GlukyUser user, String reportPath) throws IOException {
        reportPath = "resources/reports/test.pdf";
        this.user = user;
        pdfDocument = new PdfDocument(new PdfWriter(reportPath));
        document = new Document(pdfDocument, A4);
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
        document.add(h2("Weekly report"));
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

}
