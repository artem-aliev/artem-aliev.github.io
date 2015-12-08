/**
 * Интерфейс для работы с картой
 */

interface ICard {
  // Номера карт в одной масти:
  final static int _2 = 0;
  final static int _3 = 1;
  final static int _4 = 2;
  final static int _5 = 3;
  final static int _6 = 4;
  final static int _7 = 5;
  final static int _8 = 6;
  final static int _9 = 7;
  final static int _10 = 8;
  final static int ACE = 9;
  final static int JACK = 10;
  final static int QUEEN = 11;
  final static int KING = 12;

  final static int MIN_FACE = _2;
  final static int MAX_FACE = KING;

  // Номера мастей:
  final static int SPADES = 0;
  final static int CLUBS = 1;
  final static int DIAMONDS = 2;
  final static int HEARTS = 3;

  final static int MIN_SUIT = SPADES;
  final static int MAX_SUIT = HEARTS;

  // Численное значение карты
  int getValue();
  // Внешнее представление карты
  String toString();
}

/********************************************************************/

/**
 * Интерфейс для работы с колодой карт
 */

interface IPack {
  // Стасовать колоду
  void shuffle();
  // Выбрать карту из колоды
  ICard getCard();
}

/********************************************************************/

/**
 * Интерфейс игрока
 */

interface IHand {
  // Общее число очков на руках у игрока
  int getValue();
  // Взять карту из колоды
  void getCard(IPack pack);
  // Очистить (сбросить карты)
  void clear();
  // Представление набора карт игрока в текстовом виде
  String toString();
}

interface IStrategy {
   public boolean needMore();
}


/********************************************************************/

/**
 * Реализация карты полной колоды
 */

class Card implements ICard {
  int face;      // Значение карты
  int suit;      // Масть карты

  // Конструктор создает карту по заданным значению и масти
  public Card(int f, int s) {
    face = f;  suit = s;
  }

  // Метод выдает числовое значение карты при игре в очко
  public int getValue() {
    return face == ICard.ACE ? 11 :
           face <= ICard._10 ? face + 2 :
           face - 8;
  }

  // Метод выдает внешнее представление карты
  public String toString() {
    String s = "";
    switch (face) {
      case ICard._2 : s = "Двойка"; break;
      case ICard._3 : s = "Тройка"; break;
      case ICard._4 : s = "Черверка"; break;
      case ICard._5 : s = "Пятерка"; break;
      case ICard._6 : s = "Шестерка"; break;
      case ICard._7 : s = "Семерка"; break;
      case ICard._8 : s = "Восьмерка"; break;
      case ICard._9 : s = "Девятка"; break;
      case ICard._10 : s = "Десятка"; break;
      case ICard.JACK : s = "Валет"; break;
      case ICard.QUEEN : s = "Дама"; break;
      case ICard.KING : s = "Король"; break;
      case ICard.ACE : s = "Туз"; break;
    }
    s += ' ';
    switch (suit) {
      case ICard.SPADES : s += "Пик"; break;
      case ICard.CLUBS : s += "Треф"; break;
      case ICard.DIAMONDS : s += "Бубей"; break;
      case ICard.HEARTS : s += "Червей"; break;
    }
    return s;
  }
}

/********************************************************************/

/**
 * Реализация полной колоды
 */

class Pack implements IPack {
  ICard[] cards = new ICard[52];   // Все карты колоды
  int nCount = 52;                 // Количество карт в колоде

  // Конструктор создает колоду, создавая составляющие ее карты
  public Pack() {
    for (int v = ICard.MIN_FACE; v <= ICard.MAX_FACE; v++) {
      for (int s = ICard.MIN_SUIT; s <= ICard.MAX_SUIT; s++) {
        cards[4 * v + s] = new Card(v, s);
      }
    }
  }

  // Перемешивание заключается просто в возврате всех выданных 
  // ранее карт обратно в колоду
  public void shuffle() { nCount = 52; }

  // Выдача случайной карты из колоды
  public ICard getCard() {
    int random = (int)Math.floor(Math.random() * nCount);
    ICard card = cards[random];
    cards[random] = cards[nCount-1];
    cards[nCount-1] = card;
    nCount--;
    return card;
  }
}

/********************************************************************/

/**
 * Класс реализует игрока
 */

abstract class Hand implements IHand, IStrategy{
  ICard[] hand = new ICard[10];   // Набор карт
  int nCount = 0;                 // Общее число карт

  // Конструктор не нужен!

   public abstract boolean needMore();


  // Подсчет общего количества очков
  public int getValue() {
    int sum = 0;
    for (int i = 0;  i < nCount;  i++) {
      sum += hand[i].getValue();
    }
    return nCount == 2 && sum == 22 ? 21 : sum;
  }

  // Взятие карты из колоды
  public void getCard(IPack pack) {
    hand[nCount++] = pack.getCard();
  }

  // Сброс карт
  public void clear() { nCount = 0; }

  // Представление всех карт в текстовом виде
  public String toString() {
    String result = "";
    for (int i = 0;  i < nCount;  i++) {
      result += hand[i].toString() + '\n';
    }
    return result + "В с е г о :  " + getValue();
  }
}


class Computer extends Hand {
   public boolean needMore() {
       return getValue() <= 14 + 4 * Math.random();
   }
}

class Human extends Hand {
   public boolean needMore() {
       return BlackJack.ask("Еще?");
   }
}


/********************************************************************/

/**
 * Класс содержит программу для игры в очко с использованием
 * интерфейсов ICard, IPack и IHand
 */

public abstract class BlackJack {

  /**
   * Функция ask выдает сообщение и ожидает ответа на него в виде
   * ввода одного из символов 'y', 'Y', 'n', 'N'
   */
  public static boolean ask(String question) {
    do {
      System.out.print(question + " (Y/N) ");
      try {
        byte[] buffer = new byte[2];
        System.in.read(buffer);
        char reply = (char)buffer[0];
        if (Character.toLowerCase(reply) == 'y') return true;
        if (Character.toLowerCase(reply) == 'n') return false;
      } catch (java.io.IOException e) {
        return false;
      }
    } while (true);
  }

  /**
   * Основная функция реализующая процесс игры
   */
  public static void play() {
    IPack pack = new Pack();          // Колода карт
    Hand human = new Human();         // Игрок - человек
    Hand computer = new Computer();  // Игрок - компьютер (держит банк)
    int scoreHuman = 0;
    int scoreComputer = 0;

    System.out.println("Играем в очко!");
    // Сначала карты набирает игрок:
    human.getCard(pack);
    do {
      human.getCard(pack);
      System.out.println("У Вас на руках:");
      System.out.println(human.toString());
      scoreHuman = human.getValue();
      if (scoreHuman >= 22) {
        System.out.println("Перебор! Вы проиграли.");
        return;
      } else if (scoreHuman == 21) {
        System.out.println("Очко! Вы выиграли!");
        return;
      }
    } while (human.needMore());

    // Теперь карты набирает компьютер - банкир
    do {
      computer.getCard(pack);
    } while (computer.needMore());
    System.out.println("А вот что у меня...");
    System.out.println(computer.toString());
    scoreComputer = computer.getValue();

    if (scoreComputer > 21) {
      System.out.println("У меня перебор... Я проиграл.");
    } else if (scoreComputer < scoreHuman) {
      System.out.println("Да, у Вас побольше... Поздравляю!");
    } else {
      System.out.println("На этот раз моя взяла!");
    }
  }

  /**
   * Функция main организует общий процесс игры
   */
  public static void main(String[] args) {
    System.out.println("Программа игры в очко. Здравствуйте!");
    do {
      play();
    } while (ask("Сыграем еще?"));
    System.out.println("До встречи за игровым столом!");
  }
}
