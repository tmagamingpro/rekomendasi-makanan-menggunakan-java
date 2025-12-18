import java.util.*;

public class MenuRekomendasiDiskrit {

    //  DATA MENU 
    static class Item {
        String nama;
        int harga;
        Item(String n, int h) { nama = n; harga = h; }
    }

    static List<Item> makanan = Arrays.asList(
        new Item("Nasi Kemangi", 12000),
        new Item("Nasi Kerang", 15000),
        new Item("Nasi Teriyaki", 20000),
        new Item("Nasi Goreng", 20000),
        new Item("Roti Bakar", 15000),
        new Item("Batagor", 10000)
    );

    static List<Item> minuman = Arrays.asList(
        new Item("Es Teh", 3000),
        new Item("Air Putih", 1000),
        new Item("Es Jeruk", 5000),
        new Item("Kopi", 5000),
        new Item("Es Serut", 8000)
    );


    // KOMBINASI GENERIK (C(n,k))
    static <T> void kombinasiRekursif(List<T> arr, int k, int idx, List<T> cur, List<List<T>> out) {
        if (cur.size() == k) {
            out.add(new ArrayList<>(cur));
            return;
        }
        for (int i = idx; i < arr.size(); i++) {
            cur.add(arr.get(i));
            kombinasiRekursif(arr, k, i + 1, cur, out);
            cur.remove(cur.size() - 1);
        }
    }

    static <T> List<List<T>> kombinasi(List<T> arr, int k) {
        List<List<T>> hasil = new ArrayList<>();
        kombinasiRekursif(arr, k, 0, new ArrayList<>(), hasil);
        return hasil;
    }


    //  PIE (ATURAN TERLARANG) 
    static boolean kombinasiValidPIE(List<Item> combo, List<Set<String>> aturanTerlarang) {
        Set<String> nama = new HashSet<>();
        for (Item i : combo) nama.add(i.nama);

        for (Set<String> rule : aturanTerlarang) {
            if (nama.containsAll(rule)) return false;
        }
        return true;
    }

    //  FILTER BOOLEAN 
    static boolean filterBoolean(List<Item> makananList, List<Item> minumanList,
                                 boolean murah, boolean pedas, boolean manis, boolean mahal,
                                 int batasBudget) {

        int total = 0;
        for (Item m : makananList) total += m.harga;
        for (Item d : minumanList) total += d.harga;

        boolean kategoriMurah = total <= batasBudget;
        boolean kategoriMahal = total > batasBudget;

        boolean kategoriPedas = makananList.stream().anyMatch(
            x -> {
                String nama = x.nama.toLowerCase();
                return nama.contains("goreng") || nama.contains("kerang");
            }
        );

        boolean kategoriManis = minumanList.stream().anyMatch(
            x -> {
                String nama = x.nama.toLowerCase();
                return nama.contains("serut") || nama.contains("jeruk");
            }
        );

        if (!pedas && kategoriPedas) return false;
        if (!manis && kategoriManis) return false;

        boolean hasil = true;

        if (murah) hasil &= kategoriMurah;
        if (mahal) hasil &= kategoriMahal;
        if (pedas) hasil &= kategoriPedas;
        if (manis) hasil &= kategoriManis;

        return hasil;
    }


    //  UTILITY TAMPIL MENU 
    static void tampilkanMenuMakanan() {
        System.out.println("\n=== Daftar Makanan ===");
        for (int i = 0; i < makanan.size(); i++)
            System.out.println((i+1) + ". " + makanan.get(i).nama + " (" + makanan.get(i).harga + ")");
    }

    static void tampilkanMenuMinuman() {
        System.out.println("\n=== Daftar Minuman ===");
        for (int i = 0; i < minuman.size(); i++)
            System.out.println((i+1) + ". " + minuman.get(i).nama + " (" + minuman.get(i).harga + ")");
    }


    //  MAIN 
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Sistem Rekomendasi Menu (Kombinatorika + PIE + Boolean) ===");

        tampilkanMenuMakanan();
        tampilkanMenuMinuman();

        //  MODE 
        System.out.println("\nPilih mode:");
        System.out.println("1. Mode normal (1 makanan + 1 minuman)");
        System.out.println("2. Mode custom (x makanan + y minuman)");
        System.out.print("Pilihan: ");
        int mode = sc.nextInt();

        int jumlahM = 1;
        int jumlahD = 1;

        if (mode == 2) {
            System.out.print("\nJumlah makanan dalam kombinasi: ");
            jumlahM = sc.nextInt();
            System.out.print("Jumlah minuman dalam kombinasi: ");
            jumlahD = sc.nextInt();
        }

        if (jumlahM < 1 || jumlahM > makanan.size() ||
            jumlahD < 1 || jumlahD > minuman.size()) {

            System.out.println("Jumlah tidak valid melebihi ukuran menu!");
            sc.close();
            return;
        } 

        //  Aturan Terlarang (PIE) 
        System.out.print("\nBerapa banyak kombinasi terlarang? ");
        int t = sc.nextInt();
        List<Set<String>> aturanTerlarang = new ArrayList<>();

        sc.nextLine();
        for (int i = 1; i <= t; i++) {
            System.out.println("\nMasukkan nama item terlarang (pisahkan dengan koma): ");
            String[] parts = sc.nextLine().split(",");
            Set<String> rule = new HashSet<>();
            for (String p : parts) rule.add(p.trim());
            aturanTerlarang.add(rule);
        }

        //  Preferensi Boolean 
        System.out.println("\n=== Preferensi Pengguna ===");
        boolean murah  = readBool(sc, "Ingin murah? ");
        boolean pedas  = readBool(sc, "Ingin pedas? ");
        boolean manis  = readBool(sc, "Ingin manis? ");
        boolean mahal  = readBool(sc, "Ingin mahal? ");
        System.out.print("Batas budget: ");
        int budget = sc.nextInt();

        //  KOMBINASI 

        List<List<Item>> kombinasiM = kombinasi(makanan, jumlahM);
        List<List<Item>> kombinasiD = kombinasi(minuman, jumlahD);

        int totalValidPIE = 0;
        int totalValidBoolean = 0;

        System.out.println("\n=== Hasil Kombinasi Valid ===");

        for (List<Item> cm : kombinasiM) {
            for (List<Item> cd : kombinasiD) {

                List<Item> gabungan = new ArrayList<>();
                gabungan.addAll(cm);
                gabungan.addAll(cd);

                if (!kombinasiValidPIE(gabungan, aturanTerlarang))
                    continue;

                totalValidPIE++;

                if (filterBoolean(cm, cd, murah, pedas, manis, mahal, budget)) {
                    totalValidBoolean++;
                    System.out.print("- ");
                    for (Item x : gabungan) System.out.print(x.nama + " ");
                    int harga = gabungan.stream().mapToInt(i -> i.harga).sum();
                    System.out.println("(Total: " + harga + ")");
                }
            }
        }

        System.out.println("\n--- Ringkasan ---");
        System.out.println("Kombinasi valid setelah PIE: " + totalValidPIE);
        System.out.println("Kombinasi valid setelah Boolean: " + totalValidBoolean);

        sc.close();
    }


    static boolean readBool(Scanner sc, String p) {
        System.out.print(p);
        String x = sc.next().toLowerCase();
        return x.equals("true") || x.equals("t") || x.equals("ya") || x.equals("y");
    }
}