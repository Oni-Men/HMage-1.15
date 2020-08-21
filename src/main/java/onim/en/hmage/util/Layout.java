package onim.en.hmage.util;

public class Layout {
  public enum LayoutVertival {
    TOP(0),
    CENTER(2),
    BOTTOM(1);

    public final int f;

    private LayoutVertival(int f) {
      this.f = f;
    }

  }

  public enum LayoutHorizontal {
    LEFT(0),
    CENTER(2),
    RIGHT(1);

    public final int f;

    private LayoutHorizontal(int f) {
      this.f = f;
    }
  }

  public enum LayoutDirection {
    VERTICAL(0),
    HORIZONTAL(1);

    public final int f;

    private LayoutDirection(int f) {
      this.f = f;
    }
  }

  private LayoutHorizontal layoutHorizontal;
  private LayoutVertival layoutVertical;
  private LayoutDirection layoutDirection;

  public static Layout getLayout() {
    return getLayout(0);
  }

  public static Layout getLayout(int code) {
    return new Layout(code);
  }

  private Layout(int code) {
    int x = code & 3;
    int y = code >> 3 & 3;
    int d = code >> 6 & 1;

    if (x == 0)
      this.left();
    else if (x == 1)
      this.right();
    else if (x == 2)
      this.centerx();

    if (y == 0)
      this.top();
    else if (y == 1)
      this.bottom();
    else if (y == 2)
      this.centery();

    if (d == 0)
      this.vertical();
    else if (d == 1)
      this.horizontal();
  }

  public int getCode() {
    return layoutHorizontal.f | layoutVertical.f << 3 | layoutDirection.f << 6;
  }

  public Layout left() {
    this.layoutHorizontal = LayoutHorizontal.LEFT;
    return this;
  }

  public Layout right() {
    this.layoutHorizontal = LayoutHorizontal.RIGHT;
    return this;
  }

  public Layout top() {
    this.layoutVertical = LayoutVertival.TOP;
    return this;
  }

  public Layout bottom() {
    this.layoutVertical = LayoutVertival.BOTTOM;
    return this;
  }

  public Layout centerx() {
    this.layoutHorizontal = LayoutHorizontal.CENTER;
    return this;
  }

  public Layout centery() {
    this.layoutVertical = LayoutVertival.CENTER;
    return this;
  }

  public Layout vertical() {
    this.layoutDirection = LayoutDirection.VERTICAL;
    return this;
  }

  public Layout horizontal() {
    this.layoutDirection = LayoutDirection.HORIZONTAL;
    return this;
  }

  public Layout toggleDirection() {
    if (isHorizontal()) {
      this.layoutDirection = LayoutDirection.VERTICAL;
    } else {
      this.layoutDirection = LayoutDirection.HORIZONTAL;
    }
    return this;
  }

  public LayoutHorizontal getLayoutX() {
    return this.layoutHorizontal;
  }

  public LayoutVertival getLayoutY() {
    return this.layoutVertical;
  }

  public LayoutDirection getDirection() {
    return this.layoutDirection;
  }

  public boolean isRight() {
    return this.layoutHorizontal == LayoutHorizontal.RIGHT;
  }

  public boolean isCenterX() {
    return this.layoutHorizontal == LayoutHorizontal.CENTER;
  }

  public boolean isBottom() {
    return this.layoutVertical == LayoutVertival.BOTTOM;
  }

  public boolean isCenterY() {
    return this.layoutVertical == LayoutVertival.CENTER;
  }

  public boolean isHorizontal() {
    return this.layoutDirection == LayoutDirection.HORIZONTAL;
  }
}
