package Reseau ;
import java.awt.Color;
	import java.awt.Dimension;
	import java.awt.GridLayout;
	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.io.PrintWriter;
	import java.net.ServerSocket;
	import java.net.Socket;
	import java.net.SocketAddress;
	import java.util.ArrayList;

	import javax.swing.*;

	public class Jeu extends JFrame {
		private JButton[][] buttons = new JButton[10][10];
		private ArrayList<ArrayList<Integer>> p = new ArrayList<ArrayList<Integer>>();
		private ServerSocket serveurSocket;
		private Socket socket;
		private BufferedReader input;
		private PrintWriter output;
		private boolean tourActuel = true;

		public Jeu(ArrayList<ArrayList<Integer>> pions, Reseau reseau) throws IOException {
			this.p = pions;
			if (reseau == Reseau.Client) {
				socket = new Socket("localhost", 12345);
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("Client accepté");
			} else {
				// Si c'est le serveur
				serveurSocket = new ServerSocket(12345);
				socket = serveurSocket.accept();
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("Serveur sous écoute");
			}
			envoyerliste();
			recevoirliste();
			

			
			this.setSize(new Dimension(600, 600));
			this.setTitle("Jeu"+ reseau.toString());
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setVisible(true);
			JPanel panel = new JPanel();

			panel.setLayout(new GridLayout(10, 10));
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					int ii = i;
					int jj = j;
					JButton b = new JButton();
					buttons[i][j] = b;
					b.setBackground(Color.BLUE);
					buttons[i][j].addActionListener(e -> {
						if (!tourActuel) {
							return;
						}
                        try {
                            bonneCase(ii, jj, buttons[ii][jj]);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        output.println("TOUR");
						tourActuel = false;
						setGrilleActive(false);
					});


					aGagné();
					panel.add(b);

				}


			}


			this.setContentPane(panel);


			new Thread(() -> {
				try {
					while (true) {
						String message = input.readLine();
						if (message != null) {
							if (message.equals("TOUR")) {
								SwingUtilities.invokeLater(() -> {
									tourActuel = true;
									setGrilleActive(true);
								});
							} else if (message.equals("VICTOIRE")) {
								SwingUtilities.invokeLater(() -> {
									JOptionPane.showMessageDialog(null, "L'autre joueur a gagné!", "Défaite", JOptionPane.INFORMATION_MESSAGE);
                                    try {
                                        relance(); // Ferme la fenêtre et lance le menu
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();



		}

		public void bonneCase(int x, int y, JButton e) throws IOException {

			ArrayList ar = new ArrayList<>();
			ar.add(x);
			ar.add(y);
			if (this.p.contains(ar)) {
				e.setBackground(Color.RED);
				p.remove(ar);

			} else {
				e.setBackground(Color.GREEN);


			}
			if (aGagné()) {
				JOptionPane.showMessageDialog(null, "t as gagné ! ", "Vide", JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
				socket.close();
				serveurSocket.close();
				input.close();
				output.close();
			}

		}

		public boolean aGagné() {
			System.out.println(this.p);
			if (this.p.isEmpty()) {
				output.println("VICTOIRE");
				return true;
			} else {
				return false;
			}


		}

		private void setGrilleActive(boolean active) {
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					buttons[i][j].setEnabled(active && buttons[i][j].getBackground() == Color.BLUE);  // Seules les cases bleues sont activées
				}
			}
		}

		public void relance() throws IOException {
			socket.close();
			if(serveurSocket!=null){
				serveurSocket.close();
			}

			input.close();
			output.close();
			this.dispose();


		}
		
		public void envoyerliste() {
		    StringBuilder sb = new StringBuilder();
		    for (ArrayList<Integer> pion : p) {
		        sb.append(pion.get(0)).append(",").append(pion.get(1)).append(";");
		    }
		    String message = "LISTE:" + sb.toString();
		    System.out.println("Envoi de la liste : " + message);
		    output.println(message);
		}

		public void recevoirliste() throws IOException {
		    String message = input.readLine();
		    System.out.println("Message reçu : " + message);

		    if (message != null && message.startsWith("LISTE:")) {
		        String listeString = message.substring(6);
		        System.out.println("Liste parsée : " + listeString);
		        String[] pionsString = listeString.split(";");
		        p.clear();

		        for (String pionString : pionsString) {
		            if (!pionString.isEmpty()) {
		                String[] coordonnees = pionString.split(",");
		                ArrayList<Integer> pion = new ArrayList<>();
		                pion.add(Integer.parseInt(coordonnees[0]));
		                pion.add(Integer.parseInt(coordonnees[1]));
		                p.add(pion);
		            }
		        }
		        System.out.println("Nouvelle liste p : " + p);
		    } else {
		        System.out.println("Message non conforme ou null.");
		    }
		}







	}
