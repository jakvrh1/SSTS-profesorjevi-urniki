import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfUrnik {

	/*
	 * SSTS_EASISTENT_URL se uporablja kot osnovni spletni naslov kateremu
	 * dodamo končnico iz seznama stevilkeRazredov, da dobimo popolne
	 * naslove easistent urnikov
	 */
	static private final String SSTS_EASISTENT_URL =
			 "https://www.easistent.com/urniki/a193cdd53a5b8a19124e6ab27e40820343804650/razredi/";
	private String[] stevilkeRazredov = new String[] { "48485", "86045", "86055", "48475", "86065", "48495",
			"86075", "117074", "85815", "85825", "85835", "85905", "85915", "85965", "85975", "85845",
			"85855", "86035", "85925", "85935", "85985", "85995", "85865", "85875", "86005", "86015",
			"85945", "85885", "85895", "86025" };

	/*
	 * Primer vnosa: <J. Vrhovec ; J. Vrhovec, št. Učilnice, št.Dneva, št.Ure :
	 * J. Vrhovec, št.Učilnice, št.Dneva, št.Ure > V primeru lahko razberemo, da
	 * je vrednost razdeljena z : (dvopičjem). Sama vrednost pa se razdeli z
	 * , (vejico).
	 */
	private Map<String, String> tedenskiUrnik = new LinkedHashMap<>();

	public ProfUrnik() {
		podatkiVsehRazredov();
	}

	public Map<String, String> tedenskiUrnik() {
		return new LinkedHashMap<String, String>(tedenskiUrnik);
	}

	/*
	 * S to metodo dobimo HTML datoteko razreda, katerega je spletni naslov
	 * sestavljen s SSTS_EASISTENT_URL + stevilkeRazredov[indeks]
	 */
	private String HTMLRazreda(int indeks) {
		StringBuilder HTML = new StringBuilder("");
		URL naslovSpletneStrani = null;
		try {
			naslovSpletneStrani = new URL(SSTS_EASISTENT_URL + stevilkeRazredov[indeks]);
			HttpURLConnection spletnaPovezava = (HttpURLConnection) naslovSpletneStrani.openConnection();

			if (spletnaPovezava.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader bralnikHTML = new BufferedReader(
						new InputStreamReader(spletnaPovezava.getInputStream(), "UTF-8"));

				String s;
				while ((s = bralnikHTML.readLine()) != null)
					HTML.append(s);

				bralnikHTML.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return HTML.toString();
	}

	private void podatkiVsehRazredov() {
		for (int i = 0; i < stevilkeRazredov.length; ++i)
			profesorjevUrnik(HTMLRazreda(i));
	}

	/*
	 * Sprehodi se čez HTML String od profesorja. HTML kodo razdeli na HTML
	 * odseke tako, da imamo na koncu lažje berljive delčke kode. Vse delčke
	 * kode pošlje naprej k metodi dodajProfesorjevUrnik()
	 */
	private void profesorjevUrnik(String HTML) {
		int ura = 0;
		int dan = 0;

		String[] seznam = HTML.split("<td width=\"18%\"|Legenda:");

		for (int i = 1; i < seznam.length - 1; ++i) {
			dodajProfesorjevUrnik(seznam[i], ++dan, ura);
			if (dan == 5) {
				dan = 0;
				++ura;
			}
		}
	}

	/*
	 * V primeru, da Map<String, String> nima še ključa, ključ doda. V
	 * primeru, da že ima ključ in neko vsebino, vsebino samo doda, k
	 * prejšni vsebini, ključa pa ne doda.
	 */
	private void vstaviTedenskiUrnik(String str1, String str) {
		if (tedenskiUrnik.get(str1) == null)
			tedenskiUrnik.put(str1, str);
		else
			tedenskiUrnik.put(str1, tedenskiUrnik.get(str1) + ":" + str);

	}

	/*
	 * Sprehodi se čez delček HTML kode ter preveri ali je ura odpadla in
	 * kateri profesor poučuje uro. Profesorja ter njegovo uro doda tako, da
	 * kliče metodo vstaviTedenskiUrnik()
	 */
	private void dodajProfesorjevUrnik(String delcekHTML, int dan, int ura) {
		String[] predmet = delcekHTML.split("class=\"text14 bold\"><span title=\"");
		String[] profesor = delcekHTML.split("<div class=\"text11\">\\s+");

		for (int i = 0; i < predmet.length && i < profesor.length; ++i) {
			boolean aliJeUraOdpadla = false;

			/* Preveri, če ura odpade */
			Matcher odpadlaUra = Pattern.compile("title=\"Odpadla ura\"").matcher(predmet[i]);
			while (odpadlaUra.find())
				aliJeUraOdpadla = true;

			/*
			 * Če se profesor[i] začne z id="ednevnik-seznam"
			 * preskoči zabeleženje zapisa, saj je v tem primeru
			 * ničelni zapis
			 */
			String s = null;
			if (!profesor[i].startsWith(" id=\"ednevnik-seznam")) {
				s = profesor[i].substring(0, profesor[i].indexOf("</div>"));
				s = s.replaceAll("\\s", "");
			}

			if (s == null) continue;

			else if (aliJeUraOdpadla) {
				String imePriimekProf = s.replaceAll(",.*", "");
				vstaviTedenskiUrnik(imePriimekProf,
						imePriimekProf + ",Odpadla ura" + "," + dan + "," + ura);
			}

			else {
				String imePriimekProf = s.replaceAll(",.*", "");
				vstaviTedenskiUrnik(imePriimekProf, s + "," + dan + "," + ura);
			}
		}
	}

	/*
	 * static public void main(String[] args) { new ProfUrnik();
	 * 
	 * }
	 */

}
