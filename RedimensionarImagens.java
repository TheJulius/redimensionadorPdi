import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class RedimensionarImagens {

	private static String TIPO_IMAGEM = "jpg";
	private static String IMAGEM_ORIGINAL = "./teste.jpg";
	private static final String RAIZ = "./imagensFormatadas/";
	private static final int TAMANHO_QUADRADO = 256;

	private static final String NOME_IMAGEM_LARGURA_FIXA = "imagensFormatadas-largura-fixa";
	private static final String NOME_IMAGEM_ALTURA_FIXA = "imagensFormatadas-altura-fixa";
	private static final String NOME_IMAGEM_ESTICADA = "imagensFormatadas-esticada";
	private static final String REDIMENSIONAR_PELO_CENTRO = "imagensFormatadas-redimensionar-pelo-centro";
	private static final String NOME_IMAGEM_QUADRADA = "imagensFormatadas-quadrada";
	private static final String NOME_IMAGEM_QUADRADA_TRANSPARENTE = "imagensFormatadas-quadrada-transparent";
	private static final String NOME_IMAGEM_QUADRADA_BG_COLOR = "imagensFormatadas-quadrada-bg-color";
	private static final String NOME_IMAGEM_AR = "imagensFormatadas-ar";

	public static void main(String[] args) {
		try {

			// nome do arquivo da linha de comando, se fornecido
			if (args.length > 0) {
				String fileToProcess = args[0];
				if (fileToProcess != null) {
					IMAGEM_ORIGINAL = fileToProcess;
				}
			}

			File imageFile = new File(IMAGEM_ORIGINAL);
			BufferedImage bufferImage = ImageIO.read(imageFile);
			String NOME_IMAGEM_PREFIXO = imageFile.getName().substring(0, imageFile.getName().lastIndexOf('.')) + '-';

			// redimensiona a imagem do centro (imagem recortada)
			BufferedImage redimensionarDoCentro = redimensionarDoCentro(bufferImage, 480, 320);
			salvarImagem(redimensionarDoCentro, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + REDIMENSIONAR_PELO_CENTRO);

			// redimensiona a imagem com largura fixa
			BufferedImage imageWithFixedWidth = redimensionarComLarguraFixa(bufferImage, 1080);
			salvarImagem(imageWithFixedWidth, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_LARGURA_FIXA);

			// redimensiona a imagem com altura fixa
			BufferedImage imageWithFixedHeight = redimensionarComAlturaFixa(bufferImage, 1920);
			salvarImagem(imageWithFixedHeight, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_ALTURA_FIXA);

			// imagem quadrada perfeita do centro de uma imagem (imagem recortada)
			BufferedImage imagemQuadrada = redimensionarQuadrado(bufferImage, TAMANHO_QUADRADO, false, null, false);
			salvarImagem(imagemQuadrada, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_QUADRADA);

			// redimensiona a imagem completa para o quadrado, considerando a proporção e
			// configura o plano de fundo para a cor dada
			BufferedImage imagemQuadradaCentro = redimensionarQuadrado(bufferImage, TAMANHO_QUADRADO, true, Color.BLACK,
					false);
			salvarImagem(imagemQuadradaCentro, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_QUADRADA_BG_COLOR);

			// redimensiona a imagem em porcentagem
			BufferedImage imageWithAspectRatio = redimensionarPorcentagem(bufferImage, 10);
			salvarImagem(imageWithAspectRatio, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_AR);
			
			// imagem transparente com imagem completa redimensionada
			TIPO_IMAGEM = "png";
			BufferedImage imagemQuadradaFT = redimensionarQuadrado(bufferImage, TAMANHO_QUADRADO, true, null, true);
			salvarImagem(imagemQuadradaFT, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_QUADRADA_TRANSPARENTE);

			// redimensiona esticando a imagem
			BufferedImage ImagemEsticada = redimensionar(bufferImage, TAMANHO_QUADRADO, TAMANHO_QUADRADO);
			salvarImagem(ImagemEsticada, TIPO_IMAGEM, NOME_IMAGEM_PREFIXO + NOME_IMAGEM_ESTICADA);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static Rectangle getDimensaoQuadrado(BufferedImage bufferImage) {
		int imgW = bufferImage.getWidth();
		int imgH = bufferImage.getHeight();

		int startX = 0;
		int startY = 0;

		int newW = imgW;
		int newH = imgH;

		if (imgW > imgH) {
			newW = imgH;
			// se a largura for maior, então X precisa se mover
			startX = (int) Math.ceil((imgW - newW) / 2);
		} else if (imgW < imgH) {
			newH = imgW;
			// se a largura for maior, então Y precisa se mover
			startY = (int) Math.ceil((imgH - newH) / 2);
		}
		return new Rectangle(startX, startY, newW, newH);
	}

	private static BufferedImage redimensionarComLarguraFixa(BufferedImage bufferImage, double larguraMaxima) {
		int alturaEmEscala = (int) Math.ceil((larguraMaxima * bufferImage.getHeight()) / bufferImage.getWidth());
		return redimensionar(bufferImage, (int) larguraMaxima, alturaEmEscala);
	}

	private static BufferedImage redimensionarComAlturaFixa(BufferedImage bufferImage, double alturaMaxima) {
		int larguraEmEscala = (int) Math.ceil((alturaMaxima * bufferImage.getWidth()) / bufferImage.getHeight());
		return redimensionar(bufferImage, larguraEmEscala, (int) alturaMaxima);
	}

	private static BufferedImage redimensionarQuadrado(BufferedImage bufferImage, int tamanhoImagem,
			boolean imagemQuadrada, Color corDeFundo, boolean ativarTransparencia) {
		if (imagemQuadrada) {
			BufferedImage img = null;
			int xPos = 0;
			int yPos = 0;
			if (bufferImage.getWidth() > bufferImage.getHeight()) {
				img = redimensionarComLarguraFixa(bufferImage, tamanhoImagem);
				yPos = (tamanhoImagem - img.getHeight()) / 2;
			} else if (bufferImage.getHeight() > bufferImage.getWidth()) {
				img = redimensionarComAlturaFixa(bufferImage, tamanhoImagem);
				xPos = (tamanhoImagem - img.getWidth()) / 2;
			}
			if (img != null) {
				boolean ehTransparente = (corDeFundo == null && ativarTransparencia);
				BufferedImage squareImage = new BufferedImage(tamanhoImagem, tamanhoImagem,
						(ehTransparente) ? BufferedImage.TYPE_INT_ARGB : img.getType());
				Graphics2D g2d = squareImage.createGraphics();
				if (!ehTransparente) {
					g2d.setPaint(corDeFundo);
					g2d.fillRect(0, 0, tamanhoImagem, tamanhoImagem);
				}
				g2d.drawImage(img.getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_SMOOTH), xPos, yPos,
						null);
				g2d.dispose();
				return squareImage;
			}
			return null;
		}
		// obter um quadrado do centro de uma imagem
		Rectangle imgDimension = getDimensaoQuadrado(bufferImage);
		// cortar imagem de dimensão quadrada
		BufferedImage croppedImg = bufferImage.getSubimage(imgDimension.x, imgDimension.y, imgDimension.width,
				imgDimension.height);
		// reduzir o tamanho do quadrado conforme necessário e retornar
		return redimensionar(croppedImg, tamanhoImagem, tamanhoImagem);
	}

	private static BufferedImage redimensionar(BufferedImage img, int largura, int altura) {
		Image tmp = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
		BufferedImage redimensionado = new BufferedImage(largura, altura, img.getType());
		Graphics2D g2d = redimensionado.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return redimensionado;
	}

	private static BufferedImage redimensionarDoCentro(BufferedImage bufferImage, int largura, int altura) {
		if (bufferImage.getHeight() < altura || bufferImage.getWidth() < largura) {
			System.out.println("Imagem muito pequena!");
			return null;
		}

		int xPos = (bufferImage.getWidth() - largura) / 2;
		int yPos = (bufferImage.getHeight() - altura) / 2;
		return bufferImage.getSubimage(xPos, yPos, largura, altura);
	}

	private static BufferedImage redimensionarPorcentagem(BufferedImage bufferImage, double porcentagem) {
		int larguraEmEscala = (int) (bufferImage.getWidth() * (porcentagem / 100));
		int alturaEmEscala = (int) (bufferImage.getHeight() * (porcentagem / 100));
		return redimensionar(bufferImage, larguraEmEscala, alturaEmEscala);
	}

	private static void salvarImagem(BufferedImage bufferedImage, String tipoImagem, String pathArquivo) {
		try {
			File diretorio = new File(RAIZ);
			if (!diretorio.exists()) {
				diretorio.mkdir();
			}
			File arquivoOut = new File(RAIZ + pathArquivo + '.' + tipoImagem);
			ImageIO.write(bufferedImage, tipoImagem, arquivoOut);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}