import streams.People;
import streams.Sex;

import java.util.*;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {


        TreeMap<Integer, Double> seqTimeTable = new TreeMap<>();
        TreeMap<Integer, Double> paralTimeTable = new TreeMap<>();
        for (int i = 100; i <= 10_000_000; i *= 10) {
            seqTimeTable.put(i, null);
            paralTimeTable.put(i, null);
        }

        out.println("Расчеты в последовательных потоках");
        fillingSeqTimeTable(seqTimeTable);

        out.println("\nРасчеты в параллельных потоках");
        fillingParalTimeTable(paralTimeTable);

        out.println(seqTimeTable);
        out.println(paralTimeTable);
        seqTimeTable.keySet().forEach(
                (key) -> {
                    out.printf(Locale.US, "%s%d=%9.2f%s", key == 100 ? "{" : "", key,
                            paralTimeTable.get(key) / seqTimeTable.get(key) * 100,
                            key == 10_000_000 ? "}" : ", ");
                }
        );
    }

    private static void fillingParalTimeTable(TreeMap<Integer, Double> TimeTable) {
        for (int range : TimeTable.keySet()) {
            List<People> peoples = getPeople(range);
            long startTime = System.nanoTime();
            parallelCalc(peoples);
            long stopTime = System.nanoTime();
            double processTime = (double) (stopTime - startTime) / 1_000_000_000.0;
            out.printf("Process time: %.5f s\n", processTime);
            TimeTable.put(range, processTime);
        }
    }

    private static void fillingSeqTimeTable(TreeMap<Integer, Double> TimeTable) {
        for (int range : TimeTable.keySet()) {
            List<People> peoples = getPeople(range);
            long startTime = System.nanoTime();
            seqvCalc(peoples);
            long stopTime = System.nanoTime();
            double processTime = (double) (stopTime - startTime) / 1_000_000_000.0;
            out.printf("Process time: %.5f s\n", processTime);
            TimeTable.put(range, processTime);
        }
    }

    private static List<People> getPeople(int range) {
        out.printf("\nКоличество людей: %,d\n", range);
        List<String> names = Arrays.asList("Иванов", "Петров", "Сидоров");
        List<People> peoples = new ArrayList<>();

        for (int i = 0; i < range; i++) {
            peoples.add(new People(names.get(
                    new Random().nextInt(names.size())),
                    new Random().nextInt(100),
                    Sex.randomSex()));
        }
        return peoples;
    }

    private static void seqvCalc(List<People> peoples) {
        out.printf("Количество мужчин-военнообязанных: %,d\n",
                peoples.stream()
                        .filter(x -> x.getSex() == Sex.MAN)
                        .filter(x -> (x.getAge() >= 18 && x.getAge() < 66))
                        .count());

        peoples.stream()
                .filter(x -> x.getSex() == Sex.MAN)
                .mapToInt(x -> x.getAge())
                .average()
                .ifPresentOrElse(
                        v -> out.printf("Средний возраст мужчин: %.1f лет\n", v),
                        () -> out.println("В исходной выборке мужчины не представлены")
                );

        out.printf("Потенциально работоспособные люди: %,d\n",
                peoples.stream()
                        .filter(x -> (x.getAge() >= 18 && (x.getAge() < 66 && x.getSex() == Sex.MAN) ||
                                x.getAge() < 61 && x.getSex() == Sex.WOMEN))
                        .count());
    }

    private static void parallelCalc(List<People> peoples) {
        out.printf("Количество мужчин-военнообязанных: %,d\n",
                peoples.parallelStream()
                        .filter(x -> x.getSex() == Sex.MAN)
                        .filter(x -> (x.getAge() >= 18 && x.getAge() < 66))
                        .count());

        peoples.parallelStream()
                .filter(x -> x.getSex() == Sex.MAN)
                .mapToInt(x -> x.getAge())
                .average()
                .ifPresentOrElse(
                        v -> out.printf("Средний возраст мужчин: %.1f лет\n", v),
                        () -> out.println("В исходной выборке мужчины не представлены")
                );

        out.printf("Потенциально работоспособные люди: %,d\n",
                peoples.parallelStream()
                        .filter(x -> (x.getAge() >= 18 && (x.getAge() < 66 && x.getSex() == Sex.MAN) ||
                                x.getAge() < 61 && x.getSex() == Sex.WOMEN))
                        .count());
    }

}
