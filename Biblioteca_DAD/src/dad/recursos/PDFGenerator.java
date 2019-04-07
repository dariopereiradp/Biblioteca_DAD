package dad.recursos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFGraphics;
import com.qoppa.pdfWriter.PDFPage;

import dad.biblioteca.Emprestimo;
import dad.biblioteca.Livro;
import dad.biblioteca.User;

/**
 * Library <a href="https://www.qoppa.com/pdfwriter/">jPDFWriter</a>
 * 
 * @author Dário Pereira
 *
 */
public class PDFGenerator {
	
	private Emprestimo emprestimo;
	
	public PDFGenerator(Emprestimo emprestimo){
		this.emprestimo = emprestimo;
	}
	
	public PDFDocument generatePDF(){
		PDFDocument pdfDoc = new PDFDocument();
		
		double width = 8.3*72;
		double height = 11.7*72;
		
		Paper p = new Paper();
		p.setSize(width, height);
		p.setImageableArea(0, 0, 8.3*72, 11.7*72);
		PageFormat pf = new PageFormat();
		pf.setPaper(p);
		
		PDFPage page = pdfDoc.createPage(pf);
		pdfDoc.addPage(page);
		
		PDFGraphics g2d = (PDFGraphics) page.createGraphics();
		
		ImageIcon dad_icon;
		dad_icon = new ImageIcon(getClass().getResource("DAD.jpg"));
		g2d.drawImage(dad_icon.getImage(), (int)(width/2)-dad_icon.getIconWidth()/12, 10, dad_icon.getIconWidth()/6, dad_icon.getIconHeight()/6, null);	
		
		g2d.setFont(PDFGraphics.HELVETICA.deriveFont(20f).deriveFont(Font.BOLD));
		g2d.setColor(Color.BLACK);
		String text = "BIBLIOTECA DÁDIVA DE DEUS";
		int sWidth = g2d.getFontMetrics(PDFGraphics.HELVETICA.deriveFont(20f).deriveFont(Font.BOLD)).stringWidth(text);
		g2d.drawString(text, (int)(width/2)-sWidth/2, 120);
		
		g2d.setFont(PDFGraphics.COURIER.deriveFont(15f).deriveFont(Font.BOLD));
		text = "Empréstimo nº" + emprestimo.getId();
		sWidth = g2d.getFontMetrics(PDFGraphics.COURIER.deriveFont(15f).deriveFont(Font.BOLD)).stringWidth(text);
		g2d.drawString(text, (int)(width/2)-sWidth/2, 150);
		
		 
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Color.black);
		g2d.drawRoundRect((int)width/8, 220, (int)((width/8)*6), 350, 10, 10);
		
		//TODO: Tipo, titulo, id, if instanceof livros -> autor, editora, classificacao, 
		// imagem do item, data do emprestimo, data limite, número de dias,  nome do utilizador que emprestou, cpf e nome da pessoa, etc
		
		//Atenção: Após a data limite de entrega será cobrado um valor de MULTA por cada dia de atraso!
		
		g2d.setFont(PDFGraphics.COURIER.deriveFont(9f).deriveFont(Font.PLAIN));
		text = "Atenção: Após a data limite de entrega será cobrado um valor de " + Emprestimo.MULTA + "R$ por cada dia de atraso!";
		sWidth = g2d.getFontMetrics(PDFGraphics.COURIER.deriveFont(9f).deriveFont(Font.PLAIN)).stringWidth(text);
		g2d.drawString(text, (int)(width/2)-sWidth/2, 590);
		
		g2d.setFont(PDFGraphics.COURIER.deriveFont(6f).deriveFont(Font.PLAIN));
		text = "Gerado automaticamente em " + new SimpleDateFormat("dd/MMM/yyyy 'às' HH:mm:ss").format(new Date());
		sWidth = g2d.getFontMetrics(PDFGraphics.COURIER.deriveFont(6f).deriveFont(Font.PLAIN)).stringWidth(text);
		g2d.drawString(text, (int)(width-sWidth-width/10), (int)(height-height/15));
		
		
		try {
			pdfDoc.saveDocument("teste.pdf");
			System.out.println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pdfDoc;
	}

	public static void main(String[] args) {
		new PDFGenerator(new Emprestimo(User.newUser("Teste", 18, 123456789), new Livro("AAA", "Lewis", "Java", "Livre", "LL2"), System.currentTimeMillis(), 10)).generatePDF();
	}
}
