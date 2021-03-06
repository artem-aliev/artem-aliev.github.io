/**
 * ��������� ��� ������ � ������
 */

interface ICard {
  // ������ ���� � ����� �����:
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

  // ������ ������:
  final static int SPADES = 0;
  final static int CLUBS = 1;
  final static int DIAMONDS = 2;
  final static int HEARTS = 3;

  final static int MIN_SUIT = SPADES;
  final static int MAX_SUIT = HEARTS;

  // ��������� �������� �����
  int getValue();
  // ������� ������������� �����
  String toString();
}

/********************************************************************/

/**
 * ��������� ��� ������ � ������� ����
 */

interface IPack {
  // ��������� ������
  void shuffle();
  // ������� ����� �� ������
  ICard getCard();
}

/********************************************************************/

/**
 * ��������� ������
 */

interface IHand {
  // ����� ����� ����� �� ����� � ������
  int getValue();
  // ����� ����� �� ������
  void getCard(IPack pack);
  // �������� (�������� �����)
  void clear();
  // ������������� ������ ���� ������ � ��������� ����
  String toString();
}

interface IStrategy {
   public boolean needMore();
}


/********************************************************************/

/**
 * ���������� ����� ������ ������
 */

class Card implements ICard {
  int face;      // �������� �����
  int suit;      // ����� �����

  // ����������� ������� ����� �� �������� �������� � �����
  public Card(int f, int s) {
    face = f;  suit = s;
  }

  // ����� ������ �������� �������� ����� ��� ���� � ����
  public int getValue() {
    return face == ICard.ACE ? 11 :
           face <= ICard._10 ? face + 2 :
           face - 8;
  }

  // ����� ������ ������� ������������� �����
  public String toString() {
    String s = "";
    switch (face) {
      case ICard._2 : s = "������"; break;
      case ICard._3 : s = "������"; break;
      case ICard._4 : s = "��������"; break;
      case ICard._5 : s = "�������"; break;
      case ICard._6 : s = "��������"; break;
      case ICard._7 : s = "�������"; break;
      case ICard._8 : s = "���������"; break;
      case ICard._9 : s = "�������"; break;
      case ICard._10 : s = "�������"; break;
      case ICard.JACK : s = "�����"; break;
      case ICard.QUEEN : s = "����"; break;
      case ICard.KING : s = "������"; break;
      case ICard.ACE : s = "���"; break;
    }
    s += ' ';
    switch (suit) {
      case ICard.SPADES : s += "���"; break;
      case ICard.CLUBS : s += "����"; break;
      case ICard.DIAMONDS : s += "�����"; break;
      case ICard.HEARTS : s += "������"; break;
    }
    return s;
  }
}

/********************************************************************/

/**
 * ���������� ������ ������
 */

class Pack implements IPack {
  ICard[] cards = new ICard[52];   // ��� ����� ������
  int nCount = 52;                 // ���������� ���� � ������

  // ����������� ������� ������, �������� ������������ �� �����
  public Pack() {
    for (int v = ICard.MIN_FACE; v <= ICard.MAX_FACE; v++) {
      for (int s = ICard.MIN_SUIT; s <= ICard.MAX_SUIT; s++) {
        cards[4 * v + s] = new Card(v, s);
      }
    }
  }

  // ������������� ����������� ������ � �������� ���� �������� 
  // ����� ���� ������� � ������
  public void shuffle() { nCount = 52; }

  // ������ ��������� ����� �� ������
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
 * ����� ��������� ������
 */

abstract class Hand implements IHand, IStrategy{
  ICard[] hand = new ICard[10];   // ����� ����
  int nCount = 0;                 // ����� ����� ����

  // ����������� �� �����!

   public abstract boolean needMore();


  // ������� ������ ���������� �����
  public int getValue() {
    int sum = 0;
    for (int i = 0;  i < nCount;  i++) {
      sum += hand[i].getValue();
    }
    return nCount == 2 && sum == 22 ? 21 : sum;
  }

  // ������ ����� �� ������
  public void getCard(IPack pack) {
    hand[nCount++] = pack.getCard();
  }

  // ����� ����
  public void clear() { nCount = 0; }

  // ������������� ���� ���� � ��������� ����
  public String toString() {
    String result = "";
    for (int i = 0;  i < nCount;  i++) {
      result += hand[i].toString() + '\n';
    }
    return result + "� � � � � :  " + getValue();
  }
}


class Computer extends Hand {
   public boolean needMore() {
       return getValue() <= 14 + 4 * Math.random();
   }
}

class Human extends Hand {
   public boolean needMore() {
       return BlackJack.ask("���?");
   }
}


/********************************************************************/

/**
 * ����� �������� ��������� ��� ���� � ���� � ��������������
 * ����������� ICard, IPack � IHand
 */

public abstract class BlackJack {

  /**
   * ������� ask ������ ��������� � ������� ������ �� ���� � ����
   * ����� ������ �� �������� 'y', 'Y', 'n', 'N'
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
   * �������� ������� ����������� ������� ����
   */
  public static void play() {
    IPack pack = new Pack();          // ������ ����
    Hand human = new Human();         // ����� - �������
    Hand computer = new Computer();  // ����� - ��������� (������ ����)
    int scoreHuman = 0;
    int scoreComputer = 0;

    System.out.println("������ � ����!");
    // ������� ����� �������� �����:
    human.getCard(pack);
    do {
      human.getCard(pack);
      System.out.println("� ��� �� �����:");
      System.out.println(human.toString());
      scoreHuman = human.getValue();
      if (scoreHuman >= 22) {
        System.out.println("�������! �� ���������.");
        return;
      } else if (scoreHuman == 21) {
        System.out.println("����! �� ��������!");
        return;
      }
    } while (human.needMore());

    // ������ ����� �������� ��������� - ������
    do {
      computer.getCard(pack);
    } while (computer.needMore());
    System.out.println("� ��� ��� � ����...");
    System.out.println(computer.toString());
    scoreComputer = computer.getValue();

    if (scoreComputer > 21) {
      System.out.println("� ���� �������... � ��������.");
    } else if (scoreComputer < scoreHuman) {
      System.out.println("��, � ��� ��������... ����������!");
    } else {
      System.out.println("�� ���� ��� ��� �����!");
    }
  }

  /**
   * ������� main ���������� ����� ������� ����
   */
  public static void main(String[] args) {
    System.out.println("��������� ���� � ����. ������������!");
    do {
      play();
    } while (ask("������� ���?"));
    System.out.println("�� ������� �� ������� ������!");
  }
}
