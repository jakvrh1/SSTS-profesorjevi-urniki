import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIProfesorji extends JFrame {

	private ProfUrnik profesorjevUrnik = new ProfUrnik();
	private Map<String, String> tedenskiUrnik = profesorjevUrnik.tedenskiUrnik();

	private JPanel panelaUr, panelaProfesorjevihUr;
	private JComboBox<String> spustniSeznamProfesorjev, spustniSeznamDnevov;

	/*
	 * dejanjeSpustnegaSeznama nam omogoči dejanje, ko z miško kliknemo na
	 * spustni seznam ter izberemo vsebino. S tem omogočimo spremembo ur, ki
	 * so prikazane za izbran dan in izbranega profesorja.
	 */
	private ActionListener dejanjeSpustnegaSeznama = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent dogodek) {
			panelaProfesorjevihUr.removeAll();
			panelaProfesorjevihUr.revalidate();

			String dan = ((Integer) (spustniSeznamDnevov.getSelectedIndex() + 1)).toString();
			String profesor = spustniSeznamProfesorjev.getSelectedItem().toString();

			String profesorjevUrnik = tedenskiUrnik.get(profesor);
			String[] noviUrnik = new String[11];

			/*
			 * Sprehodimo se čez vse profesorjeve ure. Če se izbrani
			 * dan ujema z dnevom pri vsebini se ura zapiše v seznam
			 * noviUrnik
			 */
			for (String profesorjevaUra : profesorjevUrnik.split(":")) {
				if (profesorjevaUra.split(",")[2].equalsIgnoreCase(dan)) {
					Integer ura = new Integer(profesorjevaUra.split(",")[3]);
					noviUrnik[ura] = profesorjevaUra.split(",")[1];
				}
			}

			/*
			 * V primeru, da ima noviUrnik[indeks] vsebino jo
			 * prikažemo na grafičnem uporabniškem vmesniku tako, da
			 * dodamo JLabel z št. ure, drugače dodamo samo neviden
			 * razmik. Box.createRigidArea(new Dimension(vrednost,
			 * vrednost)) uporabimo za neviden razmik.
			 * 
			 */
			for (String profesorjevaUra : noviUrnik) {
				if (profesorjevaUra == null)
					panelaProfesorjevihUr.add(Box.createRigidArea(new Dimension(5, 50)));
				else {
					JLabel ura = new JLabel(profesorjevaUra);
					ura.setFont(new Font("Mono", Font.PLAIN, 24));
					panelaProfesorjevihUr.add(ura);
					panelaProfesorjevihUr.add(Box.createRigidArea(new Dimension(5, 20)));
				}
			}

			panelaProfesorjevihUr.revalidate();
			panelaProfesorjevihUr.repaint();

		}
	};

	public GUIProfesorji() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(350, 650);
		setResizable(false);
		getContentPane().setLayout(null);

		spustniSeznamProfesorjev = new JComboBox<String>();
		spustniSeznamProfesorjev.setBounds(12, 12, 152, 24);
		spustniSeznamProfesorjev.addActionListener(dejanjeSpustnegaSeznama);
		getContentPane().add(spustniSeznamProfesorjev);

		spustniSeznamDnevov = new JComboBox<String>();
		spustniSeznamDnevov.setBounds(176, 12, 133, 24);
		spustniSeznamDnevov.addActionListener(dejanjeSpustnegaSeznama);
		getContentPane().add(spustniSeznamDnevov);

		panelaUr = new JPanel();
		panelaUr.setBounds(12, 48, 152, 722);
		panelaUr.setLayout(new BoxLayout(panelaUr, BoxLayout.PAGE_AXIS));
		getContentPane().add(panelaUr);

		panelaProfesorjevihUr = new JPanel();
		panelaProfesorjevihUr.setBounds(176, 48, 160, 722);
		panelaProfesorjevihUr.setLayout(new BoxLayout(panelaProfesorjevihUr, BoxLayout.PAGE_AXIS));
		getContentPane().add(panelaProfesorjevihUr);

		prikazUr();
		inicializacijaSpustnihSeznamov();

		setVisible(true);
	}

	/*
	 * Doda vrednosti spustnemu seznamu spustniSeznamProfesorjev in
	 * spustniSeznamDnevov.
	 */
	private void inicializacijaSpustnihSeznamov() {
		/*
		 * Vse profesorje dodamo po abecedi zvrščene v
		 * spustniSeznamProfesorjev
		 */
		List<String> profesorji = new LinkedList<String>();
		for (Entry<String, String> s : tedenskiUrnik.entrySet())
			profesorji.add(s.getKey());

		Collections.sort(profesorji);
		for (String s : profesorji)
			spustniSeznamProfesorjev.addItem(s);

		spustniSeznamProfesorjev.revalidate();
		spustniSeznamProfesorjev.repaint();

		for (String s : "ponedeljek torek sreda četrtek petek".split(" "))
			spustniSeznamDnevov.addItem(s);

		spustniSeznamDnevov.revalidate();
		spustniSeznamDnevov.repaint();

	}

	/*
	 * Metoda doda ure na grafični uporabniški vmesnik. Npr.: predura,
	 * 1.ura, 2.ura...
	 */
	private void prikazUr() {
		JLabel predura = new JLabel("Predura");
		predura.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(predura);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel prvaUra = new JLabel("1. ura");
		prvaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(prvaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel drugaUra = new JLabel("2. ura");
		drugaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(drugaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel tretjaUra = new JLabel("3. ura");
		tretjaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(tretjaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel cetrtaUra = new JLabel("4. ura");
		cetrtaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(cetrtaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel petaUra = new JLabel("5. ura");
		petaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(petaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel sestaUra = new JLabel("6. ura");
		sestaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(sestaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel sedmaUra = new JLabel("7. ura");
		sedmaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(sedmaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel osmaUra = new JLabel("8. ura");
		osmaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(osmaUra);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel devetaura = new JLabel("9. ura");
		devetaura.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(devetaura);

		panelaUr.add(Box.createRigidArea(new Dimension(5, 20)));

		JLabel desetaUra = new JLabel("10. ura");
		desetaUra.setFont(new Font("Mono", Font.PLAIN, 24));
		panelaUr.add(desetaUra);

		panelaUr.repaint();
	}

	static public void main(String[] args) {
		new GUIProfesorji();
	}
}
