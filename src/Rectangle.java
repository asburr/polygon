// Rectangle is top right x,y coordinates and bottom left x,y.

import java.util.Comparator;
import java.util.Set;
import java.util.ArrayList;
import java.lang.RuntimeException;

public class Rectangle implements Comparator<Rectangle>,Comparable<Rectangle> {
  public double tx,ty; // Top right
  public double bx,by; // bottom left
  public String data=null;
  public Rectangle(double bx,double by,double tx,double ty) {
    if (tx < bx) throw new RuntimeException("Top x "+tx+" must be greater than bottom x "+bx);
    if (ty < by) throw new RuntimeException("Top y "+ty+" must be greater than bottom y "+by);
    this.bx=bx;
    this.by=by;
    this.tx=tx;
    this.ty=ty;
  }
  public Rectangle(Rectangle o) {
    this.bx=o.bx;
    this.by=o.by;
    this.tx=o.tx;
    this.ty=o.ty;
  }
  // Rectangles are never within other rectangles.
  public int compareTo(Rectangle o) {
    if (this.bx < o.bx) return -1;
    if (this.by < o.by) return -1;
    if (this.tx > o.tx) return 1;
    if (this.ty > o.ty) return 1;
    return 0;
  }
  public int compare(Rectangle o1,Rectangle o2) {
    return o1.compareTo(o2);
  }
  public boolean outside(double x, double y) {
    return ((x < this.bx) ||
            (y < this.by) ||
            (x > this.tx) ||
            (y > this.ty));
  }
  public boolean outside(Rectangle o) {
    return (((o.tx < this.bx) || (o.ty < this.by)
            ) && (
             (o.bx > this.tx) || (o.by > this.ty)));
  }
  public boolean same(Rectangle o) {
    return ((o.bx == this.bx) &&
            (o.by == this.by) &&
            (o.tx == this.tx) &&
            (o.ty == this.ty));
  }
  public void expand(double x, double y) {
    if (x < this.bx ) this.bx=x;
    if (y < this.by ) this.by=y;
    if (x > this.tx ) this.tx=x;
    if (y > this.ty ) this.ty=y;
  }
  public void expand(Rectangle o) {
    if (o.bx < this.bx ) this.bx=o.bx;
    if (o.by < this.by ) this.by=o.by;
    if (o.tx > this.tx ) this.tx=o.tx;
    if (o.ty > this.ty ) this.ty=o.ty;
  }
  public double area() { // Length by hight!
    return (tx-bx)*(ty-by);
  }
  // Merge two rectangles that are directly above, below, right or left of this
  // rectangle.
  // If the two rectangles are the same dimentions along the adjoining line, the
  // merge is clean in that nothing remains when the two rectangles are joined.
  // A wider rectangle joined with a narrowerer rectangle results in remaining rectanlges.
  //    +----Top right
  //    |      |
  //    bottom-+
  //    left
  // Scenario a/ (1=3, 2=7) => (3=9, (4=1, 5=1)=2), merged cos 3 is larger than 1 and larger than 2.
  //Y   +---+         +---+
  //|   | 1 |         |   |
  //| +-+---+-+  =  +-+ 3 +-+
  //| |   2   |     |4|   |5|
  //| +-------+     +-+---+-+
  //+------------------------X
  // Scenario b/ (1=3, 2=7) => (3=9, (4=1, 5=1)=2). merged cos 3 is larger than 1 and larger than 2.
  //  +-------+     +-+---+-+
  //  |   2   |     |4|   |5|
  //  +-+---+-+  =  +-+ 3 +-+
  //    | 1 |         |   |
  //    +---+         +---+
  // Scenario c/ (2=10, 1=3) !=> (3=6, (4=2, 5=2)=4), !merged cos 3 is smaller than 2.
  //   +--+           +--+
  //   |  |           | 4|
  //   |  +---+       +--+---+
  //   |2 | 1 |  =    |   3  |
  //   |  +---+       +--+---+
  //   |  |           | 5|
  //   +--+           +--+
  // Scenario d/ (2=10, 1=3) !=> (3=6, (4=2, 5=2)=4). !merged cos 3 is smaller than 2.
  //      +--+         +--+
  //      |  |         | 4|
  //  +---+  |     +---+--+
  //  | 1 | 2|   = |   3  |
  //  +---+  |     +---+--+
  //      |  |         | 5|
  //      +--+         +--+
  // The merge completes when the area of the merged rectangle is more than the largest
  // of the two rectangles being merged.
  public Rectangle merge(
    Rectangle r2, // Rectangle to merge into this.
    ArrayList<Rectangle> fragments // Remaining fragments from the merge process.
  ) {
    fragments.clear();
    Rectangle r1=this;
    Rectangle r3=new Rectangle(r1);
    Rectangle r4=null;
    Rectangle r5=null;
    boolean a=(r1.by == r2.ty);
    boolean b=(r1.ty == r2.by);
    boolean c=(r1.tx == r2.bx);
    boolean d=(r1.tx == r2.bx);
    if (a) {
      r3.by=r2.by;
      if (r2.bx < r1.bx) {
        r4=new Rectangle(r2);
        r4.tx=r1.bx;
      }
      if (r1.tx < r2.tx) {
        r5=new Rectangle(r2);
        r5.bx=r1.tx;
      }
    } else if (b) {
      r3.ty=r2.ty;
      if (r2.bx < r1.bx) {
        r4=new Rectangle(r2);
        r4.tx=r1.bx;
      }
      if (r1.tx < r2.tx) {
        r5=new Rectangle(r2);
        r5.bx=r1.tx;
      }
    } else if (c) {
      r3.bx=r2.bx;
      if (r2.ty < r1.ty) {
        r4=new Rectangle(r2);
        r4.by=r1.ty;
      }
      if (r1.by < r2.by) {
        r5=new Rectangle(r2);
        r5.ty=r1.by;
      }
    } else if (d) {
      r3.tx=r2.tx;
      if (r2.ty < r1.ty) {
        r4=new Rectangle(r2);
        r4.by=r1.ty;
      }
      if (r1.by < r2.by) {
        r5=new Rectangle(r2);
        r5.ty=r1.by;
      }
    } else {
      // Rectangles are not together.
      return null;
    }
    double a1=r1.area();
    double a2=r2.area();
    double a3=r3.area();
    if (a1 > a2) {
      if (a1 < a3) {
        if (r4!=null) fragments.add(r4);
        if (r5!=null) fragments.add(r5);
        return r3;
      }
    } else {
      if (a2 < a3) {
        if (r4!=null) fragments.add(r4);
        if (r5!=null) fragments.add(r5);
        return r3;
      }
    }
    return null;
  }
  public void print() {
    System.out.println("Bottom "+bx+","+by+" top "+tx+","+ty);
  }
  public void move(double bx,double by,double tx,double ty) {
    this.bx=bx;
    this.by=by;
    this.tx=tx;
    this.ty=ty;
  }
  public void moveX(double x) {
    this.bx+=x;
    this.tx+=x;
  }
  public void moveX(double bx,double tx) {
    this.bx+=bx;
    this.tx+=tx;
  }
  public void moveY(double y) {
    this.by+=y;
    this.ty+=y;
  }
  public void moveY(double by,double ty) {
    this.by+=by;
    this.ty+=ty;
  }
  public void move(Rectangle o) {
    this.bx=o.bx;
    this.by=o.by;
    this.tx=o.tx;
    this.ty=o.ty;
  }
  public Rectangle factory() {
    return new Rectangle(this.bx,this.by,tx,this.ty);
  }
}
