package vizsga;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Dolgozat {
	private String fileNeve;
	private int parosokSzama;
	private HashMap<Integer, Integer> szamHalmaz;
	private int egyesekSzama;
	private int kilencvenekSzama;
	private Scanner sc;
	private int[][] egyEvbenMindenSzambol;
	private final static int NYEROSZAM_DB = 5;


	public Dolgozat(String fileNeve) {
		super();
		this.fileNeve = fileNeve;
		this.parosokSzama = 0;
		this.szamHalmaz = setSzamHalmaz();
		this.egyesekSzama = 0;
		this.kilencvenekSzama = 0;
		this.sc = new Scanner(System.in);
		this.egyEvbenMindenSzambol = new int[52][NYEROSZAM_DB];
	}

	public void start() {
		fileBeolvasas(fileNeve);
		hetetBekerNyeroszamokatKiir();
		System.out.println("3. feladat: Ebben az évben " + parosokSzama + " alkalommal húztak páros számot.\n");
		System.out.println(
				"4. feladat: Ebben az évben a köv. számokat NEM húzták ki egyszer sem: " + kiNemHuzottSzamok() + "\n");
		System.out.println("5. feladat: Ebben az évben " + egyesekSzama + " alkalommal húzták ki az 1-es számot.\n");
		System.out
				.println("6. feladat: Ebben az évben " + kilencvenekSzama + " alkalommal húzták ki a 90-es számot.\n");
		System.out.println(
				"7. feladat: Ebben az évben legsűrűbben a " + azOtLegsurubbenKihuzott() + " húzták ki.\n");
		System.out.println("8. feladat: Ebben az évben legritkábban, de minimum egyszer a " + azOtLegritkabbanKihuzott()
				+ " húzták ki.\n");
		System.out.println("9. feladat, a táblázat:\n");
		tablazatKirajzol();
		

	}
	
	private void tablazatKirajzol() {
		for (int j = 0; j < 4; j++) {
			if (j == 0) {
				for (int i = 0; i < 31; i++) {
					tablazatKiir(j * 30 + i);
				}
				System.out.println();
			} else {
				for (int i = 0; i < 31; i++) {
					if (i == 0) {
						System.out.print(j + "  ");
					} else {
						tablazatKiir(hanyszorHuztak(((j - 1) * 30 + i)));
						// tablazatKiir((j - 1) * 30 + i);
					}
				}
				System.out.println();
			}
		}
	}

	private void tablazatKiir(int n) {
		if (n < 10) {
			System.out.print(n + "  ");
		} else {
			System.out.print(n + " ");
		}
	}

	private int hanyszorHuztak(int key) {
		Iterator<Map.Entry<Integer, Integer>> iterator = szamHalmaz.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> aktSzam = iterator.next();
			if (aktSzam.getKey() == key) {
				return aktSzam.getValue();
			}
		}
		return -1;
	}

	private String azOtLegritkabbanKihuzott() {
		Iterator<Map.Entry<Integer, Integer>> iterator = szamHalmaz.entrySet().iterator();
		int[] otLegRitkabb = new int[NYEROSZAM_DB];
		int[] legRitkabbHanyszor = new int[NYEROSZAM_DB];
		String result = "";

		for (int i = 0; i < 5; i++) {
			otLegRitkabb[i] = 91;
			legRitkabbHanyszor[i] = 91;
		}

		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> aktSzam = iterator.next();

			for (int i = 0; i < 5; i++) {
				if (otLegRitkabb[i] > aktSzam.getValue() && aktSzam.getValue() > 0) {
					for (int j = 3; j >= i; j--) {
						otLegRitkabb[j + 1] = otLegRitkabb[j];
						legRitkabbHanyszor[j + 1] = legRitkabbHanyszor[j];
					}
					otLegRitkabb[i] = aktSzam.getKey();
					legRitkabbHanyszor[i] = aktSzam.getValue();
					i = 5; // for ciklus kilepo feltetele
				}
			}
		}
		for (int i = 0; i < 5; i++) {
			result += otLegRitkabb[i] + "-t " + legRitkabbHanyszor[i] + "x, ";
		}
		return result;
	}

	private String azOtLegsurubbenKihuzott() {
		Iterator<Map.Entry<Integer, Integer>> iterator = szamHalmaz.entrySet().iterator();
		int[] otLegSurubb = new int[NYEROSZAM_DB];
		int[] legSurubbHanyszor = new int[NYEROSZAM_DB];
		String result = "";

		for (int i = 0; i < 5; i++) {
			otLegSurubb[i] = 0;
			legSurubbHanyszor[i] = 0;
		}

		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> aktSzam = iterator.next();

			for (int i = 0; i < 5; i++) {
				if (otLegSurubb[i] < aktSzam.getValue()) {
					for (int j = 3; j >= i; j--) { // leptetem a tovabbi ertekeket
						legSurubbHanyszor[j + 1] = legSurubbHanyszor[j];
						otLegSurubb[j + 1] = otLegSurubb[j];
					}
					legSurubbHanyszor[i] = aktSzam.getValue();
					otLegSurubb[i] = aktSzam.getKey();
					i = 5; // kilepofeltetel a for ciklusbol
				}
			}
		}
		for (int i = 0; i < 5; i++) {
			result += otLegSurubb[i] + "-t " + legSurubbHanyszor[i] + "x, ";
		}

		return result;
	}

	private String kiNemHuzottSzamok() {
		List<Integer> rosszSzamok = new ArrayList<>();
		String result = "";

		Iterator<Map.Entry<Integer, Integer>> iterator = szamHalmaz.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> aktSzam = iterator.next();
			if (aktSzam.getValue() == 0) {
				rosszSzamok.add(aktSzam.getKey());
			}
		}

		Collections.sort(rosszSzamok);
		if (rosszSzamok.size() > 0) {
			for (int i = 0; i < rosszSzamok.size(); i++) {
				if (i < rosszSzamok.size() - 1) {
				result += rosszSzamok.get(i) + ", ";
				} else {
					result += rosszSzamok.get(i);
				}
			}
		}
		return result;
	}

	private void hetetBekerNyeroszamokatKiir() {
		System.out.println("2. feladat:\n");
		System.out.print("Mely hét nyerőszámaira vagy kíváncsi? ");
		int aktHetet = beolvasSzamot(1, 52);
		kiIr(egyEvbenMindenSzambol[aktHetet - 1]);
		System.out.println();
	}
	
	private void fileBeolvasas(String fileNev) {
		System.out.println("1. feladat\n");
		int[] temp = new int[NYEROSZAM_DB];
		String sor = new String();
		try {
			RandomAccessFile file = new RandomAccessFile(fileNev, "r");
			int i = 0;
			while ((sor = file.readLine()) != null) {
				temp = sorbolNyeroSzamok(sor);
				egyEvbenMindenSzambol[i] = temp;
				halmazhozAd(temp);
				i++;
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("A " + fileNev + " fileban lévő adatok beolvasva, ha minden igaz.\n");
	}

	private void halmazhozAd(int[] szamok) {
		Iterator<Map.Entry<Integer, Integer>> iterator = szamHalmaz.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> aktSzam = iterator.next();
			for (int i = 0; i < szamok.length; i++) {
				if (aktSzam.getKey() == szamok[i]) {
					aktSzam.setValue((aktSzam.getValue() + 1));
				}
			}
		}
	}
	

	private HashMap<Integer, Integer> setSzamHalmaz() {
		HashMap<Integer, Integer> kiinduloHalmaz = new HashMap<>();
		for (int i = 0; i < 90; i++) {
			kiinduloHalmaz.put(i + 1, 0);
		}
		return kiinduloHalmaz;
	}

	private int[] sorbolNyeroSzamok(String sor) {
		int[] aktSzamok = new int[NYEROSZAM_DB];
		for (int i = 0; i < aktSzamok.length; i++) {
			try {
				aktSzamok[i] = Integer.parseInt(sor.split(" ")[i]);
				if ((aktSzamok[i] % 2) == 0) {
					parosokSzama++;
				}

				if (aktSzamok[i] == 1) {
					egyesekSzama++;
				}

				if (aktSzamok[i] == 90) {
					kilencvenekSzama++;
				}
			} catch (Exception e) {
				System.out.print("Valami hiba a file megnyitása/olvasásakor. ");
				e.printStackTrace();
			}
		}
		Arrays.sort(aktSzamok);
		return aktSzamok;
	}

	// csak min és max közötti értéket fogad el
	private int beolvasSzamot(int min, int max) {
		int szam = Integer.MIN_VALUE;
		// ha valami tevedes folytan min > max, akkor megcserelem oket
		if (min > max) {
			max = min + max;
			min = max - min;
			max = max - min;
		}
		// do-while ciklus azert van, hogy csak min és max között adhasson meg szamot
		do {
			// try-catch blokkban ellenorzom, hogy tenyleg szamot adott-e
			try {
				szam = Integer.parseInt(sc.nextLine());
			} catch (Exception e) {
				System.out.println("Légyszíves számjegyeket adj meg, és ne mást!");
			}

			if (!(min <= szam && szam <= max)) {
				System.out.println("A számokat légyszíves " + min + " és " + max + " között mondd!");
			}
		} // Addig nem lephet tovabb, mig jo szamot nem ad be
		while (!(min <= szam && szam <= max));
		return szam;
	}

	private void kiIr(int aktHet[]) {
		for (int i = 0; i < aktHet.length; i++) {
			System.out.print(aktHet[i]);
			if (i < (aktHet.length - 1)) {
				System.out.print(", ");
			} else {
				System.out.println(".");
			}
		}
	}
}
