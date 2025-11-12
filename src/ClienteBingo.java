import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.HashSet;

public class ClienteBingo extends JFrame { 

	private static final long serialVersionUID = 1L;

	private final int TOTAL_CELDAS = 25;
	private final int INDICE_CENTRAL = 12;

	private BingoCard miCarton; 
	private boolean lineaCantada = false;
	private boolean lineaGlobalGanada = false;
	private int ultimoNumeroSorteado = -1;
	private boolean juegoTerminado = false;

	private HashSet<Integer> numerosSorteadosDelServidor = new HashSet<>(); 

	private Socket socket;
	private BufferedReader entrada;
	private PrintWriter salida;
	private final String IP_SERVIDOR = "127.0.0.1";
	private final int PUERTO_SERVIDOR = 8888;
	private String miNombre = "Jugador" + (int)(Math.random() * 10000);

	private JPanel contentPane;
	private JButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10,
	btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19, btn20,
	btn21, btn22, btn23, btn24, btn25;

	private JLabel lblNumeroSorteado;
	private JButton btnCantarLinea;
	private JButton btnCantarBingo;
	private JButton btnNuevaPartida; 
	private JButton btnSalir; 

	private JButton[] arrayBotones; 

	
	class BingoCard {

		int[] numeros; 
		boolean[] marcados; 
		boolean[] lineaCantada;
		final int TAMANO = 5; 
		final int TOTAL_CELDAS = TAMANO * TAMANO;

		public BingoCard() {
			numeros = new int[TOTAL_CELDAS];
			marcados = new boolean[TOTAL_CELDAS];
			lineaCantada = new boolean[12];
			generarCarton();
		}

		private void generarCarton() {
			int maxRango = 75;
			int totalNumerosACarton = TOTAL_CELDAS;

			int[] posiblesNumeros = new int[maxRango]; 
			for (int i = 0; i < maxRango; i++) {
				posiblesNumeros[i] = i + 1;
			}

			for (int i = maxRango - 1; i > 0; i--) {
				int j = (int) (Math.random() * (i + 1)); 
				int temp = posiblesNumeros[i];
				posiblesNumeros[i] = posiblesNumeros[j];
				posiblesNumeros[j] = temp;
			}

			int[] numerosSeleccionados = new int[totalNumerosACarton];
			for (int i = 0; i < totalNumerosACarton; i++) {
				numerosSeleccionados[i] = posiblesNumeros[i];
			}

			Arrays.sort(numerosSeleccionados);

			for (int i = 0; i < TOTAL_CELDAS; i++) { 
				numeros[i] = numerosSeleccionados[i];
				marcados[i] = false;
			}
		}

		public int marcarNumero(int numeroABuscar) {
			for (int i = 0; i < numeros.length; i++) {
				if (numeros[i] == numeroABuscar) {
					marcados[i] = true;
					return i;
				}
			}
			return -1;
		}

		public void marcarPorIndice(int index) {
			if (index >= 0 && index < TOTAL_CELDAS) {
				marcados[index] = true;
			}
		}

		public boolean estaMarcado(int index) {
			if (index >= 0 && index < TOTAL_CELDAS) {
				return marcados[index];
			}
			return false;
		}

		private boolean verificarLinea(int startIndex, int step) {
			for (int i = 0; i < TAMANO; i++) {
				int index = startIndex + i * step;
				if (index >= marcados.length || !marcados[index]) {
					return false;
				}
			}
			return true; 
		}

		public int comprobarLineaCompleta() {
			for (int i = 0; i < TAMANO; i++) {
				int startIndex = i * TAMANO;
				if (!lineaCantada[i] && verificarLinea(startIndex, 1)) {
					lineaCantada[i] = true; 
					return i;
				}
			}

			for (int i = 0; i < TAMANO; i++) {
				int indiceLinea = i + 5;
				int startIndex = i;
				if (!lineaCantada[indiceLinea] && verificarLinea(startIndex, TAMANO)) {
					lineaCantada[indiceLinea] = true; 
					return indiceLinea;
				}
			}

			if (!lineaCantada[10] && verificarLinea(0, TAMANO + 1)) {
				lineaCantada[10] = true;
				return 10;
			}

			if (!lineaCantada[11] && verificarLinea(TAMANO - 1, TAMANO - 1)) {
				lineaCantada[11] = true;
				return 11;
			}

			return -1;
		}

		public boolean comprobarBingo() {
			for (int i = 0; i < TOTAL_CELDAS; i++) {
				if (!marcados[i]) {
					return false; 
				}
			}
			return true;
		}

		public int[] getNumeros() {
			return numeros;
		}
	}

	class BingoGame {
		int[] numerosSorteables; 
		int indiceSorteo = 0;
		public BingoGame(int maximoNumero) {
			numerosSorteables = new int[maximoNumero];
			for (int i = 0; i < maximoNumero; i++) {
				numerosSorteables[i] = i + 1; 
			}
			
		}
		public int sacarNumero() { return -1; }
		public void reiniciar() {}
	}


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteBingo frame = new ClienteBingo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public ClienteBingo() {
		miCarton = new BingoCard();
		configurarVentana();
		crearComponentes();
		arrayBotones=new JButton[TOTAL_CELDAS];
		llenarArray(arrayBotones);
		registrarEventos();
		estadoBotones(true); 
		inicializarCarton();
		conectarAlServidor();
		setTitle("Bingo-ALMI - " + miNombre);
	}

	private void conectarAlServidor() {
		new Thread(() -> {
			try {
				socket = new Socket(IP_SERVIDOR, PUERTO_SERVIDOR);
				entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				salida = new PrintWriter(socket.getOutputStream(), true);

				String mensajeServidor;
				while ((mensajeServidor = entrada.readLine()) != null) {
					procesarMensajeServidor(mensajeServidor);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "No se pudo conectar al Servidor de Bingo. Asegúrate que ServidorBingo esté corriendo y la IP sea correcta.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
			}
		}).start();
	}

	private void procesarMensajeServidor(String mensaje) {
		if (mensaje.startsWith("NUMERO:")) {
			try {
				int numeroSorteado = Integer.parseInt(mensaje.substring(7));
				numerosSorteadosDelServidor.add(numeroSorteado); 
				EventQueue.invokeLater(() -> {
					actualizarNumeroSorteado(numeroSorteado);
				});
			} catch (NumberFormatException e) {
				System.err.println("Mensaje de número inválido: " + mensaje);
			}
		} else if (mensaje.startsWith("ANUNCIO:")) {
			String anuncio = mensaje.substring(8);
			EventQueue.invokeLater(() -> {
				JOptionPane.showMessageDialog(this, anuncio, "Anuncio de la Sala", JOptionPane.INFORMATION_MESSAGE);
				
				if (anuncio.contains("NO es válido")) {
					if (anuncio.contains("LINEA")) {
						btnCantarLinea.setEnabled(true);
						lineaCantada = false;
						btnCantarLinea.setBackground(null);
					} else if (anuncio.contains("BINGO")) {
						btnCantarBingo.setEnabled(true);
					}
				}
			});
		} else if (mensaje.startsWith("SUBMITNAME")) {
			salida.println(miNombre);
		} else if (mensaje.equals("LINEA_GANADA")) {
			EventQueue.invokeLater(() -> {
				lineaGlobalGanada = true;
				juegoTerminado = false;
				btnCantarLinea.setEnabled(false);
				btnCantarLinea.setBackground(Color.GREEN);
				btnCantarLinea.setText("¡LÍNEA GANADA!");
			});
		} else if (mensaje.equals("BINGO_GANADO")) {
			EventQueue.invokeLater(() -> {
				juegoTerminado = true;
				btnCantarLinea.setEnabled(false);
				btnCantarBingo.setEnabled(false);
				btnCantarBingo.setBackground(Color.GREEN);
				if (!miCarton.comprobarBingo()) {
					JOptionPane.showMessageDialog(this, "El juego ha terminado. ¡Otro jugador cantó BINGO!", "Juego Finalizado", JOptionPane.WARNING_MESSAGE);
				}
			});
		} else if (mensaje.equals("REINICIO")) {
			EventQueue.invokeLater(() -> {
				lineaGlobalGanada = false;
				juegoTerminado = false;
				btnCantarLinea.setEnabled(true);
				btnCantarLinea.setText("Línea");
				btnCantarLinea.setBackground(null);
				btnCantarBingo.setEnabled(true);
				JOptionPane.showMessageDialog(this, "El servidor ha reiniciado la partida. Genera un cartón nuevo para unirte.", "Reinicio de Juego", JOptionPane.INFORMATION_MESSAGE);
			});
		} else if (mensaje.startsWith("PREGUNTA:")) {
			String[] partes = mensaje.substring(7).split("\\|");
			String tipoCanto = partes[0];
			String pregunta = partes[1];
			
			Object[] opciones = new Object[4];
			opciones[0] = "1) " + partes[2];
			opciones[1] = "2) " + partes[3];
			opciones[2] = "3) " + partes[4];
			opciones[3] = "4) " + partes[5];
			
			
			
			
			EventQueue.invokeLater(() -> {
				int respuestaIndex = JOptionPane.showOptionDialog(this,
						"¡Para validar tu " + tipoCanto + ", responde esta pregunta!\n\n" + pregunta,
						"Pregunta de Validación",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						opciones,
						opciones[0]);

				if (respuestaIndex != JOptionPane.CLOSED_OPTION) {
					int respuestaElegida = respuestaIndex + 1;
					salida.println("PREGUNTA_RESP:" + respuestaElegida);
				} else {
					salida.println("PREGUNTA_RESP:0"); 
				}
			});
		}
	}

	public void actualizarNumeroSorteado(int numero) {
		if (juegoTerminado) return;
		ultimoNumeroSorteado = numero;
		String numeroTexto = String.valueOf(numero);
		if (lblNumeroSorteado != null) {
			lblNumeroSorteado.setText(numeroTexto); 
		}
	}

	
	private void configurarVentana() {
		setTitle("Bingo-ALMI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null); 
	}
	private void crearComponentes() {
		JPanel panelControles = new JPanel();
		panelControles.setBounds(0, 0, 434, 70);
		panelControles.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		contentPane.add(panelControles);

		btnCantarLinea = new JButton("Línea");
		panelControles.add(btnCantarLinea);

		btnCantarBingo = new JButton("Bingo");
		panelControles.add(btnCantarBingo);

		lblNumeroSorteado = new JLabel("---", SwingConstants.CENTER);
		lblNumeroSorteado.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNumeroSorteado.setForeground(Color.RED);
		lblNumeroSorteado.setPreferredSize(new java.awt.Dimension(80, 50));
		panelControles.add(lblNumeroSorteado);

		JPanel panelSistema = new JPanel(new GridLayout(1, 2, 5, 5));
		panelSistema.setBounds(10, 360, 414, 40);
		contentPane.add(panelSistema);

		btnNuevaPartida = new JButton("Nueva Partida");
		panelSistema.add(btnNuevaPartida);

		btnSalir = new JButton("Salir");
		panelSistema.add(btnSalir);

		JPanel panelCarton = new JPanel();
		panelCarton.setBounds(0, 75, 434, 280);
		contentPane.add(panelCarton);
		panelCarton.setLayout(new GridLayout(5, 5, 2, 2));


		btn1 = new JButton(""); panelCarton.add(btn1);
		btn2 = new JButton(""); panelCarton.add(btn2);
		btn3 = new JButton(""); panelCarton.add(btn3);
		btn4 = new JButton(""); panelCarton.add(btn4);
		btn5 = new JButton(""); panelCarton.add(btn5);
		btn6 = new JButton(""); panelCarton.add(btn6);
		btn7 = new JButton(""); panelCarton.add(btn7);
		btn8 = new JButton(""); panelCarton.add(btn8);
		btn9 = new JButton(""); panelCarton.add(btn9);
		btn10 = new JButton(""); panelCarton.add(btn10);
		btn11 = new JButton(""); panelCarton.add(btn11);
		btn12 = new JButton(""); panelCarton.add(btn12);
		btn13 = new JButton("ALMI"); panelCarton.add(btn13); 
		btn14 = new JButton(""); panelCarton.add(btn14);
		btn15 = new JButton(""); panelCarton.add(btn15);
		btn16 = new JButton(""); panelCarton.add(btn16);
		btn17 = new JButton(""); panelCarton.add(btn17);
		btn18 = new JButton(""); panelCarton.add(btn18);
		btn19 = new JButton(""); panelCarton.add(btn19);
		btn20 = new JButton(""); panelCarton.add(btn20);
		btn21 = new JButton(""); panelCarton.add(btn21);
		btn22 = new JButton(""); panelCarton.add(btn22);
		btn23 = new JButton(""); panelCarton.add(btn23);
		btn24 = new JButton(""); panelCarton.add(btn24);
		btn25 = new JButton(""); panelCarton.add(btn25);
	}
	private void llenarArray(JButton[] arrayBotones) {
		arrayBotones[0] = btn1; arrayBotones[1] = btn2; arrayBotones[2] = btn3; arrayBotones[3] = btn4; arrayBotones[4] = btn5;
		arrayBotones[5] = btn6; arrayBotones[6] = btn7; arrayBotones[7] = btn8; arrayBotones[8] = btn9; arrayBotones[9] = btn10;
		arrayBotones[10] = btn11; arrayBotones[11] = btn12; arrayBotones[12] = btn13; 
		arrayBotones[13] = btn14; arrayBotones[14] = btn15; arrayBotones[15] = btn16; arrayBotones[16] = btn17;
		arrayBotones[17] = btn18; arrayBotones[18] = btn19; arrayBotones[19] = btn20; arrayBotones[20] = btn21;
		arrayBotones[21] = btn22; arrayBotones[22] = btn23; arrayBotones[23] = btn24; arrayBotones[24] = btn25;

	}
	private void estadoBotones (boolean estado) {
		for(int i=0;i<arrayBotones.length;i++) {
			arrayBotones[i].setEnabled(estado);
		}
	}

	public void registrarEventos() {
		
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}});
		btnNuevaPartida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				iniciarNuevaPartida(); 
			} 
		});

		btnCantarLinea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manejarCantarLinea();
			}
		});

		btnCantarBingo.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				manejarCantarBingo();
			}
		});

		for (int i = 0; i < TOTAL_CELDAS; i++) {
			final int index = i;
			arrayBotones[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manejarMarcadoManual(index);
				}
			});
		}
	}

	private void inicializarCarton() {
		int[] numerosCarton = miCarton.getNumeros(); 
		for (int i = 0; i < TOTAL_CELDAS; i++) {
			arrayBotones[i].setText(String.valueOf(numerosCarton[i]));
			arrayBotones[i].setBackground(Color.WHITE);
			arrayBotones[i].setFont(new Font("Arial", Font.BOLD, 14));
			arrayBotones[i].setForeground(Color.BLACK); 

			if (i == INDICE_CENTRAL) { 
				arrayBotones[i].setText("ALMI"); 
				miCarton.marcarPorIndice(INDICE_CENTRAL);
				arrayBotones[i].setBackground(Color.DARK_GRAY); 
				arrayBotones[i].setForeground(Color.WHITE);
				arrayBotones[i].setEnabled(false);
			}
		}
	}

	private void iniciarNuevaPartida() {
		if (salida != null) {
			salida.println("RESTART:" + miNombre); 
		}

		miCarton = new BingoCard(); 
		numerosSorteadosDelServidor.clear(); 
		lineaCantada = false;
		ultimoNumeroSorteado = -1;
		juegoTerminado = false;
		lineaGlobalGanada = false;

		lblNumeroSorteado.setText("---");
		btnCantarLinea.setBackground(null);
		btnCantarLinea.setText("Línea");
		btnCantarLinea.setEnabled(true);
		btnCantarBingo.setEnabled(true);
		estadoBotones(true);
		inicializarCarton();

		JOptionPane.showMessageDialog(this, "¡Nuevo cartón generado!", "Nueva Partida", JOptionPane.INFORMATION_MESSAGE);
	}

	private void manejarMarcadoManual(int indexBoton) {
		if(juegoTerminado) return;
		JButton boton = arrayBotones[indexBoton];
		if (indexBoton == INDICE_CENTRAL || miCarton.estaMarcado(indexBoton)) { return; }
		int numeroEnBoton;
		try {
			numeroEnBoton = Integer.parseInt(boton.getText());
		} catch (NumberFormatException ex) { return; }

		if (numerosSorteadosDelServidor.contains(numeroEnBoton)) {
			miCarton.marcarNumero(numeroEnBoton);
			boton.setBackground(Color.YELLOW);
			boton.setForeground(Color.BLACK);
		} else if (numerosSorteadosDelServidor.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El servidor aún no ha sorteado ningún número.", "Error de Marcado", JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "El número " + numeroEnBoton + " aún no ha sido sorteado por el Servidor.", "Error de Marcado", JOptionPane.ERROR_MESSAGE);
		}
	}


	private void manejarCantarLinea() {
		if(juegoTerminado) return;

		if (lineaGlobalGanada) {
			JOptionPane.showMessageDialog(this, "La línea ya fue cantada por otro jugador.", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (lineaCantada) {
			JOptionPane.showMessageDialog(this, "¡Ya has cantado una línea! Ahora ve por el Bingo.", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int indiceLinea = miCarton.comprobarLineaCompleta();

		if (indiceLinea != -1) {
			lineaCantada = true;
			btnCantarLinea.setBackground(Color.YELLOW);
			btnCantarLinea.setEnabled(false);

			if (salida != null) {
				salida.println("LINEA:" + miNombre); 
			}

			

		} else {
			JOptionPane.showMessageDialog(this, 
					"Aún no tienes una línea completa.", 
					"Fallo de Línea", 
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void manejarCantarBingo() {
		if(juegoTerminado) return;

		if (miCarton.comprobarBingo()) {
			
			btnCantarBingo.setEnabled(false);

			if (salida != null) {
				salida.println("BINGO:" + miNombre); 
			}

			

		} else {
			JOptionPane.showMessageDialog(this, 
					"Aún no tienes BINGO completo. ¡Debes marcar todas las casillas!", 
					"Fallo de Bingo", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
}