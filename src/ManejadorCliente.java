import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class ManejadorCliente extends Thread {
	private Socket socket;
	private PrintWriter salida;
	private String nombreJugador;
	private static ConcurrentHashMap<String, PrintWriter> clientes;

	private String cantoPendiente = null;
	private int respuestaCorrectaPendiente = -1;

	private static final String[][] PREGUNTAS_SOSTENIBILIDAD = {
			{"¿Cuál es el objetivo principal de la sostenibilidad?", 
				"Satisfacer solo las necesidades de la generación actual.", 
				"Aumentar el consumo de recursos naturales para impulsar el crecimiento económico.", 
				"Priorizar la protección del medio ambiente por encima de las necesidades sociales y económicas.", 
				"Garantizar que las necesidades del presente se satisfagan sin comprometer la capacidad de las futuras generaciones.", 
			"4"},
			{"¿Cuántos pilares o dimensiones principales tiene el desarrollo sostenible?", 
				"Cuatro: ambiental, social, económico y cultural.", 
				"Dos: ecológico y económico.", 
				"Uno: solo el medioambiental.",
				"Tres: ambiental, social y económico.",
			"4"},
			{"¿Cuál de las siguientes es una fuente de energía renovable?", 
				"Gas natural.", 
				"Petróleo.", 
				"Carbón.",
				"Eólica.",
			"4"},
			{"¿Qué acción corresponde a la 'R' de Reducir en la regla de las 3 R's (Reducir, Reutilizar, Reciclar)?", 
				"Comprar productos con menos empaques.", 
				"Usar una bolsa de tela vieja como bolsa de compras.", 
				"Separar el plástico y el vidrio en contenedores diferentes.",
				"Transformar botellas de plástico en fibra textil.",
			"1"},
			{"¿Qué significa que un recurso natural es 'no renovable'?", 
				"Que su ritmo de regeneración es mucho más lento que su ritmo de consumo humano.", 
				"Que se regenera a un ritmo más rápido de lo que se consume.", 
				"Que no puede ser utilizado en absoluto.",
				"Que su consumo no afecta el medio ambiente.",
			"1"},
			{"¿Cuál es la forma más efectiva de ahorrar electricidad al usar electrodomésticos?", 
				"Desenchufar los aparatos que no se estén utilizando.", 
				"Dejarlos conectados pero apagados.", 
				"Poner la nevera cerca del horno para aprovechar el calor.",
				"Usar bombillas halógenas en lugar de incandescentes.",
			"1"},
			{"Usar bolsas de tela para hacer las compras en lugar de bolsas de plástico es una medida de...", 
				"Retirar.", 
				"Rechazar (o Reducir).", 
				"Reparar.",
				"Reciclar.",
			"2"},
			{"¿Qué término describe la variedad de vida, incluyendo plantas, animales y microorganismos, en la Tierra?", 
				"Biomasa.", 
				"Geología.", 
				"Ecosistema.",
				"Biodiversidad.",
			"4"},
			{"¿Cuál es el principal gas de efecto invernadero emitido por la quema de combustibles fósiles (petróleo, carbón)?", 
				"Oxígeno (O2).", 
				"Nitrógeno (N2).", 
				"Dióxido de carbono (CO2).",
				"Vapor de agua (H2O).",
			"3"},
			{"¿Qué color de contenedor de reciclaje se utiliza comúnmente para el vidrio?", 
				"Amarillo.", 
				"Azul.", 
				"Gris/Negro.",
				"Verde.",
			"4"},
			{"Darle un segundo uso a una botella de plástico (por ejemplo, como maceta) es un ejemplo de...", 
				"Reducir.", 
				"Reparar.", 
				"Reciclar.",
				"Reutilizar",
			"4"},
			{"¿Cuál es un beneficio directo de utilizar el transporte público o compartir coche?", 
				"Disminuye la emisión de gases contaminantes por persona.", 
				"Aumenta la congestión del tráfico.", 
				"Incrementa el gasto en combustible por persona.",
				"Requiere la construcción de más carreteras.",
			"1"},
			{"El fenómeno del 'calentamiento global' se debe principalmente al aumento de...", 
				"Gases de efecto invernadero en la atmósfera.", 
				"La capa de ozono.", 
				"La cantidad de agua en los océanos.",
				"La radiación solar recibida.",
			"1"},
			{"¿Cuál es la práctica de gestión de residuos más preferible según la jerarquía de residuos?", 
				"Reciclaje.", 
				"Eliminación (vertido).", 
				"Prevención / Reducción.",
				"Recuperación de energía.",
			"3"},
			{"¿Qué sector genera la mayor cantidad de gases de efecto invernadero a nivel global?", 
				"Agricultura y uso del suelo.", 
				"Transporte.", 
				"Producción de electricidad y calor.",
				"Industria.",
			"3"},
			{"¿Qué es una huella de carbono?", 
				"El espacio que ocupa una fábrica en el mapa.", 
				"La marca que deja un neumático de coche en el suelo.", 
				"La totalidad de gases de efecto invernadero emitidos por un individuo u organización.",
				"La cantidad de árboles plantados.",
			"3"},
			{"¿Qué significa el término 'economía circular'?", 
				"Producir y tirar (modelo lineal).", 
				"Diseñar productos para que duren poco y se compren más a menudo.", 
				"Un sistema que busca eliminar el desperdicio y el uso continuo de recursos.",
				"Vender productos solo localmente.",
			"3"},
			{"¿Cuál es el propósito de los Objetivos de Desarrollo Sostenible (ODS) de la ONU?", 
				"Establecer metas militares globales.", 
				"Promover únicamente el crecimiento económico.", 
				"Servir como un plan para lograr un futuro mejor y más sostenible para todos.",
				"Determinar la política climática de un solo país.",
			"3"},
			{"¿Qué hace la energía solar fotovoltaica?", 
				"Convierte el viento en electricidad.", 
				"Convierte el calor del interior de la Tierra en energía.", 
				"Convierte directamente la luz solar en electricidad.",
				"Produce calor para calentar agua.",
			"3"},
			{"¿Qué material tarda más tiempo en degradarse en la naturaleza?", 
				"Papel.", 
				"Cáscara de plátano.", 
				"Vidrio.",
				"Plástico PET.",
			"3"},
			{"¿Qué se considera uno de los 17 Objetivos de Desarrollo Sostenible (ODS) de la Agenda 2030?",
					"La explotación de recursos minerales no renovables.",
					"El aumento de la producción de energía proveniente de combustibles fósiles.",
					"La salud y el bienestar.",
					"La desestabilización de los mercados financieros para impulsar la competencia.",
			"3"},
			{"¿Cuál se considera la causa principal y más significativa de la pérdida de biodiversidad en la actualidad?",
					"Caza furtiva y comercio ilegal de especies.",
					"Cambio climático.",
					"Contaminación del aire y del agua.",
					"Destrucción y fragmentación de hábitats naturales (como la deforestación y la urbanización).",
			"4"},
			{"¿Cuál de los 17 Objetivos de Desarrollo Sostenible (ODS) de la ONU se centra directamente en la protección de la biodiversidad, los bosques y la lucha contra la desertificación?",
				"ODS 13: Acción por el Clima.",
				"ODS 15: Vida de Ecosistemas Terrestres.",
				"ODS 1: Fin de la Pobreza.ODS 14: Vida Submarina.",
				"ODS 14: Vida Submarina.",
			"2"},
			{"Al ir de compras, ¿qué deberías usar para reducir la cantidad de plástico?",
				"Una bolsa de plástico nueva.",
				"Una bolsa de papel desechable.",
				"Una bolsa de tela reutilizable.",
				"Un carro de la compra de metal.",
			"3"}
	};

	public ManejadorCliente(Socket socket, ConcurrentHashMap<String, PrintWriter> clientes) {
		this.socket = socket;
		this.clientes = clientes;
	}

	private void enviarPregunta(String tipoCanto) {
		Random rand = new Random();
		int index = rand.nextInt(PREGUNTAS_SOSTENIBILIDAD.length);
		String[] pregunta = PREGUNTAS_SOSTENIBILIDAD[index];

		cantoPendiente = tipoCanto;
		respuestaCorrectaPendiente = Integer.parseInt(pregunta[5]);

		String mensajePregunta = "PREGUNTA:" + tipoCanto + "|" + pregunta[0] + "|" + pregunta[1] + "|" + pregunta[2] + "|" + pregunta[3] + "|" + pregunta[4];
		salida.println(mensajePregunta);
	}

	private void procesarRespuesta(String respuesta) {
		if (cantoPendiente == null) return;

		int respuestaCliente;
		try {
			respuestaCliente = Integer.parseInt(respuesta);
		} catch (NumberFormatException e) {
			salida.println("ANUNCIO:ERROR: Respuesta inválida recibida. (No es un número válido)");
			cantoPendiente = null;
			return;
		}

		if (respuestaCliente == respuestaCorrectaPendiente) {
			salida.println("ANUNCIO:¡Respuesta correcta! Canto válido.");
			if (cantoPendiente.equals("LINEA")) {
				if (!ServidorBingo.isLineaGanada()) {
					ServidorBingo.setLineaGanada(true);
					ServidorBingo.enviarMensajeATodos("ANUNCIO:¡LÍNEA CANTADA por " + nombreJugador + " después de responder una pregunta!");
					ServidorBingo.enviarMensajeATodos("LINEA_GANADA"); 
				} else {
					salida.println("ANUNCIO:ERROR: La línea ya fue ganada por otro jugador.");
				}
			} else if (cantoPendiente.equals("BINGO")) {
				if (!ServidorBingo.isBingoGanado()) {
					ServidorBingo.setBingoGanado(true);
					ServidorBingo.enviarMensajeATodos("ANUNCIO:¡¡BINGO!! ¡" + nombreJugador + " ha ganado la partida con una respuesta correcta!");
					ServidorBingo.enviarMensajeATodos("BINGO_GANADO"); 
				} else {
					salida.println("ANUNCIO:ERROR: El Bingo ya fue ganado por otro jugador.");
				}
			}
		} else {
			salida.println("ANUNCIO:¡Respuesta INCORRECTA! El canto de " + cantoPendiente + " NO es válido.");
		}

		cantoPendiente = null;
	}


	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			salida = new PrintWriter(socket.getOutputStream(), true);

			salida.println("SUBMITNAME");
			nombreJugador = entrada.readLine();

			if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
				nombreJugador = "JugadorAnonimo" + System.currentTimeMillis();
			}

			clientes.put(nombreJugador, salida);
			System.out.println("Nuevo jugador conectado: " + nombreJugador);

			ServidorBingo.enviarMensajeATodos("ANUNCIO:¡" + nombreJugador + " se ha unido a la partida!");

			String linea;
			while ((linea = entrada.readLine()) != null) {
				System.out.println("Mensaje de " + nombreJugador + ": " + linea);

				if (linea.startsWith("LINEA:")) {
					if (ServidorBingo.isLineaGanada()) {
						salida.println("ANUNCIO:ERROR: La línea ya fue ganada por otro jugador.");
					} else {

						enviarPregunta("LINEA");
					}

				} else if (linea.startsWith("BINGO:")) {
					if (ServidorBingo.isBingoGanado()) {
						salida.println("ANUNCIO:ERROR: El Bingo ya fue ganado por otro jugador.");
					} else {

						enviarPregunta("BINGO");
					}
				} else if (linea.startsWith("PREGUNTA_RESP:")) {

					procesarRespuesta(linea.substring(14)); 
				}
				else if (linea.startsWith("RESTART:")) {
					ServidorBingo.enviarMensajeATodos("ANUNCIO:El jugador " + nombreJugador + " ha generado un nuevo cartón.");
				}
			}
		} catch (IOException e) {
			System.out.println("Conexión perdida con " + nombreJugador);
		} finally {
			if (nombreJugador != null) {
				clientes.remove(nombreJugador);
				System.out.println(nombreJugador + " ha abandonado la partida.");
				ServidorBingo.enviarMensajeATodos("ANUNCIO:" + nombreJugador + " ha abandonado.");
			}
			try {
				socket.close();
			} catch (IOException e) { }
		}
	}
}