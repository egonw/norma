package org.xmlcml.norma.image.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.norma.Fixtures;
import org.xmlcml.norma.input.pdf.PDF2ImagesConverter;

public class ImageToHOCRConverterTest {

	public final static Logger LOG = Logger.getLogger(ImageToHOCRConverterTest.class);
	static {LOG.setLevel(Level.DEBUG);}
	
	@Test
	public void testConvert() throws Exception {
		ImageToHOCRConverter converter = new ImageToHOCRConverter();
		File infile = new File(Fixtures.TEST_PUBSTYLE_DIR, "neuro/image.2.1.Im0.png.png");
		File outfile = new File("target/neuro/image.2.1.hocr");
		converter.convertImageToHOCR(infile, outfile);
	}
	
	@Test
	public void testConvertToSVG() throws Exception {
		ImageToHOCRConverter converter = new ImageToHOCRConverter();
		File infile = new File(Fixtures.TEST_PUBSTYLE_DIR, "neuro/image.2.1.Im0.png.png");
		File outfileRoot = new File("target/neuro/image.2.1.hocr");
		converter.convertImageToHOCR(infile, outfileRoot);
		File outfile = new File("target/neuro/image.2.1.hocr.html");
		Assert.assertNotNull(outfile);
		Assert.assertTrue("outfile exists", outfile.exists());
		HOCRReader hocrReader = new HOCRReader();
		hocrReader.readHOCR(new FileInputStream(outfile));
		SVGElement svgg = hocrReader.getOrCreateSVG();
		Assert.assertNotNull("svgg not null", svgg);
		SVGSVG.wrapAndWriteAsSVG(svgg, new File("target/neuro/image.2.1.hocr.svg"));
	}
	
	@Test
	@Ignore // not fully tested for overlap
	public void testConvertPDFToSVG() throws Exception {
		File infile = new File(Fixtures.TEST_PUBSTYLE_DIR, "neuro/Chen2005.pdf");
//		File infile = new File(Fixtures.TEST_PUBSTYLE_DIR, "neuro/Maggio2009.pdf");
		analyzePDF(infile);
	}

	@Test
	public void testConvertPNGsToSVG() throws Exception {
		File ERIN_PNG = new File(Fixtures.TEST_PUBSTYLE_DIR, "neuro/erinPngs/");
		File[] pngs = new File[] { 
				
				new File(ERIN_PNG, "chen2006.png"),
				new File(ERIN_PNG, "Gant.png"),
				new File(ERIN_PNG, "MaggioSegal_PC1_DH.png"),
				new File(ERIN_PNG, "pedarzani.png"),
				new File(ERIN_PNG, "pedarzani2001.png"),
				new File(ERIN_PNG, "podlogar.png"),
				new File(ERIN_PNG, "Staff1.png"),
				new File(ERIN_PNG, "White.png"),
		};
		analyzePNG(new File("target/neuro/erinpngs"), pngs);
	}

	private void analyzePNG(File outputDir, File[] infiles) throws Exception {
		outputDir.mkdirs();
		String imageSuffix = "png";
		for (File file : infiles) {
			String root = FileUtils.basename(file.getName());
			System.out.println("-------"+root+"--------");
			BufferedImage image = ImageIO.read(file);
			HOCRReader hocrReader = new HOCRReader();
			hocrReader.setMaxFontSize(50.);
			hocrReader.labelSubImages("[A-Za-z]");
			hocrReader.createHTMLandSVG(outputDir, imageSuffix, image, root);
		}
	}

	private void analyzePDF(File infile) throws Exception {
		PDF2ImagesConverter pdf2ImagesConverter = new PDF2ImagesConverter();
		List<NamedImage> namedImageList = pdf2ImagesConverter.readPDF(new FileInputStream(infile));
		File outputDir = new File("target/neuro/images");
		outputDir.mkdirs();
		String imageSuffix = "png";
		HOCRReader hocrReader = new HOCRReader();
		hocrReader.setMaxFontSize(50.);
		hocrReader.labelSubImages("[A-Za-z]");
//		hocrReader.setImageMarginX(50);
//		hocrReader.setImageMarginY(50);
		for (NamedImage namedImage : namedImageList) {
			System.out.println("-------"+namedImage.getKey()+"--------");
			hocrReader.createHTMLandSVG(outputDir, imageSuffix, namedImage);
		}
	}


}
