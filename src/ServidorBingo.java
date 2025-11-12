import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.util.HashSet;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.BorderLayout;


public class ServidorBingo extends JFrame {

	private static final long serialVersionUID = 1L;

	private final int TOTAL_CELDAS = 75;
	private static final int PUERTO = 8888;
	private static ConcurrentHashMap<String, PrintWriter> clientes = new ConcurrentHashMap<>(); 


	private static volatile boolean lineaGanada = false;
	private static volatile boolean bingoGanado = false;


	private HashSet<Integer> numerosSorteados = new HashSet<>(); 
	private int ultimoNumeroSorteado = -1;
	private boolean juegoTerminado = false;

	private JPanel contentPane;

	private JLabel lblNumeroSorteado;

	private JButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10,
	btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19, btn20,
	btn21, btn22, btn23, btn24, btn25, btn26, btn27, btn28, btn29, btn30,
	btn31, btn32, btn33, btn34, btn35, btn36, btn37, btn38, btn39, btn40,
	btn41, btn42, btn43, btn44, btn45, btn46, btn47, btn48, btn49, btn50,
	btn51, btn52, btn53, btn54, btn55, btn56, btn57, btn58, btn59, btn60,
	btn61, btn62, btn63, btn64, btn65, btn66, btn67, btn68, btn69, btn70,
	btn71, btn72, btn73, btn74, btn75;

	private JButton btnSortear;

	private JButton[] botonesNumeros;

	public static void enviarMensajeATodos(String mensaje) {
		for (PrintWriter escritor : clientes.values()) {
			escritor.println(mensaje);
		}
	}


	public static boolean isLineaGanada() {
		return lineaGanada;
	}

	public static void setLineaGanada(boolean estado) {
		lineaGanada = estado;
	}

	public static boolean isBingoGanado() {
		return bingoGanado;
	}

	public static void setBingoGanado(boolean estado) {
		bingoGanado = estado;
	}


	private void iniciarServidor() {
		new Thread(() -> {
			try (ServerSocket listener = new ServerSocket(PUERTO)) {
				System.out.println("El Servidor de Bingo está corriendo en el puerto " + PUERTO);
				while (true) {
					new ManejadorCliente(listener.accept(), clientes).start();
				}
			} catch (IOException e) {
				System.err.println("Error al iniciar el servidor: " + e.getMessage());
			}
		}).start();
	}


	private int sortearNumeroUnico() {
		if (numerosSorteados.size() >= TOTAL_CELDAS) {
			juegoTerminado = true;
			return -1;
		}
		int numero;
		do {
			numero = (int) (Math.random() * TOTAL_CELDAS) + 1;
		} while (numerosSorteados.contains(numero));

		numerosSorteados.add(numero);
		return numero;
	}

	private void MarcarNumeroSorteado() {
		String textoSorteado = lblNumeroSorteado.getText();

		try {
			int numeroSorteado = Integer.parseInt(textoSorteado);

			for (JButton btn : botonesNumeros) {

				if (btn.getText().equals(String.valueOf(numeroSorteado))) {
					btn.setBackground(Color.GREEN);
					btn.setForeground(Color.BLACK);
					setEnabled(true);

					break;
				}
			}
		} catch (NumberFormatException e) {
		}
	}

	public HashSet<Integer> getNumerosSorteados() {
		return numerosSorteados;
	}

	public void reiniciarJuego() {
		numerosSorteados.clear();
		ultimoNumeroSorteado = -1;
		juegoTerminado = false;


		ServidorBingo.setLineaGanada(false);
		ServidorBingo.setBingoGanado(false);


		for (JButton btn : botonesNumeros) {
			btn.setBackground(null);
			btn.setForeground(Color.BLACK);
		}

		lblNumeroSorteado.setText("---");
		btnSortear.setEnabled(true);


		enviarMensajeATodos("REINICIO"); 
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServidorBingo frame = new ServidorBingo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ServidorBingo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(5, 5));

		JPanel panelCarton = new JPanel();
		panelCarton.setLayout(new GridLayout(15, 5, 0, 0));
		contentPane.add(panelCarton, BorderLayout.CENTER);

		btn1 = new JButton("1"); panelCarton.add(btn1);
		btn2 = new JButton("2"); panelCarton.add(btn2);
		btn3 = new JButton("3"); panelCarton.add(btn3);
		btn4 = new JButton("4"); panelCarton.add(btn4);
		btn5 = new JButton("5"); panelCarton.add(btn5);
		btn6 = new JButton("6"); panelCarton.add(btn6);
		btn7 = new JButton("7"); panelCarton.add(btn7);
		btn8 = new JButton("8"); panelCarton.add(btn8);
		btn9 = new JButton("9"); panelCarton.add(btn9);
		btn10 = new JButton("10"); panelCarton.add(btn10);
		btn11 = new JButton("11"); panelCarton.add(btn11);
		btn12 = new JButton("12"); panelCarton.add(btn12);
		btn13 = new JButton("13"); panelCarton.add(btn13);
		btn14 = new JButton("14"); panelCarton.add(btn14);
		btn15 = new JButton("15"); panelCarton.add(btn15);
		btn16 = new JButton("16"); panelCarton.add(btn16);
		btn17 = new JButton("17"); panelCarton.add(btn17);
		btn18 = new JButton("18"); panelCarton.add(btn18);
		btn19 = new JButton("19"); panelCarton.add(btn19);
		btn20 = new JButton("20"); panelCarton.add(btn20);
		btn21 = new JButton("21"); panelCarton.add(btn21);
		btn22 = new JButton("22"); panelCarton.add(btn22);
		btn23 = new JButton("23"); panelCarton.add(btn23);
		btn24 = new JButton("24"); panelCarton.add(btn24);
		btn25 = new JButton("25"); panelCarton.add(btn25);
		btn26 = new JButton("26"); panelCarton.add(btn26);
		btn27 = new JButton("27"); panelCarton.add(btn27);
		btn28 = new JButton("28"); panelCarton.add(btn28);
		btn29 = new JButton("29"); panelCarton.add(btn29);
		btn30 = new JButton("30"); panelCarton.add(btn30);
		btn31 = new JButton("31"); panelCarton.add(btn31);
		btn32 = new JButton("32"); panelCarton.add(btn32);
		btn33 = new JButton("33"); panelCarton.add(btn33);
		btn34 = new JButton("34"); panelCarton.add(btn34);
		btn35 = new JButton("35"); panelCarton.add(btn35);
		btn36 = new JButton("36"); panelCarton.add(btn36);
		btn37 = new JButton("37"); panelCarton.add(btn37);
		btn38 = new JButton("38"); panelCarton.add(btn38);
		btn39 = new JButton("39"); panelCarton.add(btn39);
		btn40 = new JButton("40"); panelCarton.add(btn40);
		btn41 = new JButton("41"); panelCarton.add(btn41);
		btn42 = new JButton("42"); panelCarton.add(btn42);
		btn43 = new JButton("43"); panelCarton.add(btn43);
		btn44 = new JButton("44"); panelCarton.add(btn44);
		btn45 = new JButton("45"); panelCarton.add(btn45);
		btn46 = new JButton("46"); panelCarton.add(btn46);
		btn47 = new JButton("47"); panelCarton.add(btn47);
		btn48 = new JButton("48"); panelCarton.add(btn48);
		btn49 = new JButton("49"); panelCarton.add(btn49);
		btn50 = new JButton("50"); panelCarton.add(btn50);
		btn51 = new JButton("51"); panelCarton.add(btn51);
		btn52 = new JButton("52"); panelCarton.add(btn52);
		btn53 = new JButton("53"); panelCarton.add(btn53);
		btn54 = new JButton("54"); panelCarton.add(btn54);
		btn55 = new JButton("55"); panelCarton.add(btn55);
		btn56 = new JButton("56"); panelCarton.add(btn56);
		btn57 = new JButton("57"); panelCarton.add(btn57);
		btn58 = new JButton("58"); panelCarton.add(btn58);
		btn59 = new JButton("59"); panelCarton.add(btn59);
		btn60 = new JButton("60"); panelCarton.add(btn60);
		btn61 = new JButton("61"); panelCarton.add(btn61);
		btn62 = new JButton("62"); panelCarton.add(btn62);
		btn63 = new JButton("63"); panelCarton.add(btn63);
		btn64 = new JButton("64"); panelCarton.add(btn64);
		btn65 = new JButton("65"); panelCarton.add(btn65);
		btn66 = new JButton("66"); panelCarton.add(btn66);
		btn67 = new JButton("67"); panelCarton.add(btn67);
		btn68 = new JButton("68"); panelCarton.add(btn68);
		btn69 = new JButton("69"); panelCarton.add(btn69);
		btn70 = new JButton("70"); panelCarton.add(btn70);
		btn71 = new JButton("71"); panelCarton.add(btn71);
		btn72 = new JButton("72"); panelCarton.add(btn72);
		btn73 = new JButton("73"); panelCarton.add(btn73);
		btn74 = new JButton("74"); panelCarton.add(btn74);
		btn75 = new JButton("75"); panelCarton.add(btn75);

		botonesNumeros = new JButton[] {
				btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10,
				btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19, btn20,
				btn21, btn22, btn23, btn24, btn25, btn26, btn27, btn28, btn29, btn30,
				btn31, btn32, btn33, btn34, btn35, btn36, btn37, btn38, btn39, btn40,
				btn41, btn42, btn43, btn44, btn45, btn46, btn47, btn48, btn49, btn50,
				btn51, btn52, btn53, btn54, btn55, btn56, btn57, btn58, btn59, btn60,
				btn61, btn62, btn63, btn64, btn65, btn66, btn67, btn68, btn69, btn70,
				btn71, btn72, btn73, btn74, btn75
		};

		JPanel panelControles = new JPanel();
		contentPane.add(panelControles, BorderLayout.NORTH);

		btnSortear = new JButton("Sortear");
		panelControles.add(btnSortear);

		lblNumeroSorteado = new JLabel("---", SwingConstants.CENTER);
		lblNumeroSorteado.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNumeroSorteado.setForeground(Color.RED);
		lblNumeroSorteado.setPreferredSize(new java.awt.Dimension(80, 50));
		panelControles.add(lblNumeroSorteado);


		btnSortear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!juegoTerminado && !ServidorBingo.isBingoGanado()) {
					ultimoNumeroSorteado = sortearNumeroUnico(); 

					if (ultimoNumeroSorteado != -1) {
						lblNumeroSorteado.setText(String.valueOf(ultimoNumeroSorteado));

						enviarMensajeATodos("NUMERO:" + ultimoNumeroSorteado);

						MarcarNumeroSorteado();
					} else {
						lblNumeroSorteado.setText("FIN");
						btnSortear.setEnabled(false);
						enviarMensajeATodos("ANUNCIO:¡Juego terminado! El bombo está vacío.");
					}
				}
			}
		});

		iniciarServidor();
	}
}