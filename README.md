# Laboratorium 3: Stan oraz pamiątka

Autor: Łukasz Mroszczak

## 1. Cel zadania

Celem było napisanie symulacji rozprzestrzeniania się zakażenia w populacji poruszającej się po prostokątnym obszarze. Stan zdrowotny osób miał być zrealizowany wzorcem **Stan**, a zapis i wczytanie stanu symulacji wzorcem **Pamiątka**. Do ruchu należało użyć wektorów z Laboratorium 2, a jedna sekunda symulacji to 25 kroków.

## 2. Wzorzec Stan

Wzorzec Stan zastosowałem do stanu zdrowotnego osoby. Zachowanie osoby zależy od tego, czy jest zdrowa, zakażona czy odporna, a każdy z tych stanów jest osobną klasą.

| Rola we wzorcu | Klasa | Zadanie |
| --- | --- | --- |
| Context | `Person` | Przechowuje aktualny stan i przekazuje mu wywołanie `update()` |
| State | `HealthState` | Interfejs: `update()`, `isSusceptible()`, `isInfectious()`, `transmissionProbability()` |
| ConcreteState | `HealthyState` | Zdrowy: może się zarazić, nie zaraża |
| ConcreteState | `SymptomaticState` | Zakażony z objawami: zaraża z prawdopodobieństwem 100% |
| ConcreteState | `AsymptomaticState` | Zakażony bez objawów: zaraża z prawdopodobieństwem 50% |
| ConcreteState | `ImmuneState` | Odporny: nie zaraża się i nie zaraża |

Oba stany zakażenia dziedziczą po klasie abstrakcyjnej `InfectedState`, w której jest wspólne odliczanie czasu choroby.

**Przejścia między stanami:**

1. Zdrowy → zakażony: gdy osoba przez co najmniej 3 s jest w odległości do 2 m od zakażonej i wypadnie losowanie (50% lub 100%). To przejście wykonuje klasa `Simulation`, bo wymaga dwóch osób.
2. Zakażony → odporny: po 20–30 s. To przejście wykonuje sam stan:

```java
public void update(Person person) {
    remainingSteps--;
    if (remainingSteps <= 0) {
        person.setState(new ImmuneState());
    }
}
```

Osoba nie sprawdza swojego stanu instrukcją `if` ani `switch`, tylko wywołuje `state.update(this)`. Dzięki temu dodanie nowego stanu wymaga jedynie napisania nowej klasy.

## 3. Wzorzec Pamiątka

Wzorzec Pamiątka zastosowałem do zapisu i wczytania stanu symulacji w dowolnym momencie t. Symulacja sama tworzy kopię swojego stanu, a inna klasa jedynie ją przechowuje.

| Rola we wzorcu | Klasa | Zadanie |
| --- | --- | --- |
| Originator | `Simulation` | `createMemento()` tworzy pamiątkę, `restore(m)` przywraca z niej stan |
| Memento | `SimulationMemento` | Niezmienna kopia stanu symulacji |
| CareTaker | `SimulationCareTaker` | `create()` zapisuje punkt, `restore(n)` wraca do n-tego punktu, zapis i odczyt pliku |
| Client | `MainWindow` | Przyciski zapisu i wczytania stanu |

**Zawartość pamiątki:**

- numer kroku (czas symulacji),
- osoby: położenie, prędkość, typ stanu i pozostały czas choroby (`PersonSnapshot`),
- liczniki kontaktów między parami (`ContactEntry`),
- kopia generatora liczb losowych,
- następny wolny identyfikator i konfiguracja.

Pamiątka przechowuje kopie danych, a nie odwołania do obiektów symulacji, więc dalszy ruch nie zmienia zapisanego punktu. Konstruktor i gettery pamiątki są pakietowe, więc używa ich tylko `Simulation`; `SimulationCareTaker` niczego z pamiątki nie odczytuje.

Zapis do pliku wykonuje `SimulationCareTaker` przez serializację Javy:

```java
public void saveToFile(Path path) throws IOException {
    try (OutputStream file = Files.newOutputStream(path);
         ObjectOutputStream out = new ObjectOutputStream(file)) {
        out.writeObject(originator.createMemento());
    }
}
```

## 4. Pozostałe elementy programu

- **Wektory z Lab 2:** położenie osoby to `Vector2D`, a prędkość to `Polar2DAdapter`, z którego biorę kąt i długość przy zmianie kierunku.
- **`Area` i `SimulationConfig`:** granice obszaru (50 × 50 m) oraz wszystkie parametry z polecenia.
- **`ContactTracker`:** liczy, ile kolejnych kroków każda para jest w odległości do 2 m.
- **`Simulation.step()`:** jeden krok to 1/25 s; przesuwa osoby, obsługuje granicę, zaraża i dodaje nowe osoby.
- **`MainWindow` i `SimulationPanel`:** okno Swing z kolorowymi osobami i przyciskami przypadków 1 i 2.

## 5. Wnioski

- Wzorzec Stan pozwolił uniknąć instrukcji `if`/`switch` po stanie osoby. Każdy stan sam określa swoje zachowanie, a dodanie nowego stanu wymaga tylko nowej klasy.
- Wzorzec Pamiątka umożliwił zapis stanu w dowolnym momencie bez ujawniania wnętrza symulacji. Zapisanie generatora liczb losowych sprawiło, że po wczytaniu symulacja przebiega identycznie.
- Oba przypadki różnią się tylko jednym parametrem (udziałem odpornych), więc przełączanie między nimi nie wymagało zmian w kodzie symulacji.

## Uruchomienie

Otworzyć projekt w IntelliJ IDEA i uruchomić klasę `Main` (katalog `src`). Nie są potrzebne żadne zewnętrzne biblioteki.
