import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;   
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStream;  
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

public class Polygon {
  public Rectangle rectangle=null;
  private ArrayList<Double> vertx=new ArrayList<Double>(),verty=new ArrayList<Double>();
//  private ArrayList<Integer> vertX=new ArrayList<Integer>(),vertY=new ArrayList<Integer>();
  public String data;
//  public static int coordinateToInt(double d) { return (int)(d*100000); }
  public Polygon(String line) {
    int i=line.indexOf("{");
    if (i==-1) {
      System.err.println("0 ({) Failed to understand line "+line);
      return;
    }
    data=line.substring(0,i);
    line=line.substring(i+1);
    String firstXS=null,firstYS=null;
    while (line.length()>0) {
      if (line.charAt(0) == '}') {
        break;
      }
      i=line.indexOf("{");
      if (i==-1) {
        System.err.println("6 ({) Failed to understand line "+line);
        return;
      }
      line=line.substring(i+1);
      i=line.indexOf(",");
      if (i==-1) {
        System.err.println("1 (,) Failed to understand line "+line);
        return;
      }
      String xs=line.substring(0,i);
      line=line.substring(i+1);
      i=line.indexOf("}");
      if (i==-1) {
        System.err.println("2 (}) Failed to understand line "+line);
        return;
      }
      String ys=line.substring(0,i);
      double x,y;
      try { y=Double.parseDouble(ys);
      } catch(Exception e) {
        e.printStackTrace();
        System.err.println("Failed to get double from "+ys);
        return;
      }
      try { x=Double.parseDouble(xs);
      } catch(Exception e) {
        e.printStackTrace();
        System.err.println("Failed to get double from "+xs);
        return;
      }
      if (this.rectangle==null) {
        this.rectangle=new Rectangle(x,y,x,y);
      } else {
        this.rectangle.expand(x,y);
      }
      this.vertx.add(x); this.verty.add(y);
      line=line.substring(i+1);
    }
  }
  public void print() {
    System.err.println(data);
    for (int i=0;i<this.vertx.size();i++) {
      double x=this.vertx.get(i);
      double y=this.verty.get(i);
      System.err.println("x="+x+" y="+y);
    }
  }
  public int bordering(Rectangle r) {
    // Rectangle is bordering when one point within polygon and one outside.
    // Rectangle is within polygon when all four points are in polygon.
    // Rectangle is outside polygon when all four points are outside polygon.
    int c=0;
    if (this.outside(r.bx,r.by)) c++;
    if (this.outside(r.bx,r.by)) c++;
    if (this.outside(r.bx,r.ty)) c++;
    if (this.outside(r.tx,r.by)) c++;
    if (c==0) {
      if (r.bx == this.rectangle.bx) c++;
      if (r.tx == this.rectangle.tx) c++;
      if (r.by == this.rectangle.by) c++;
      if (r.ty == this.rectangle.ty) c++;
    }
    // return (c>0 && c<4);
    return c;
  }
  public boolean outside(double testx, double testy) {
    if (rectangle.outside(testx,testy)) return true;
    int i, j;
    boolean c=false;
    for (i = 0, j = this.vertx.size()-1; i < this.vertx.size(); j = i++) {
      double xi=vertx.get(i);
      double yi=verty.get(i);
      double xj=vertx.get(j);
      double yj=verty.get(j);
      double t=(yj-yi);
      if ( ((yi>testy) != (yj>testy)) &&
  	   (testx < ( (xj-xi) * (testy-yi) / (yj-yi) + xi)) )
         c=!c;
    }
    return !c;
  }
  public Set<Rectangle> bordering_set = new TreeSet<Rectangle>();
  ArrayList<Rectangle> newbordering=new ArrayList<Rectangle>();
  public void border(double gridXWidth, double gridYWidth) {
    this.bordering_set.clear();
    this.newbordering.clear();
    double bx=vertx.get(0); // Get first coordinate.
    double by=verty.get(0);
    bx=bx-(bx%gridXWidth); // Snap coordinates to grid.
    by=by-(by%gridYWidth);
    double tx=bx+gridXWidth; // Rectangle around first coordinate.
    double ty=by+gridYWidth;
    Rectangle r=new Rectangle(bx,by,tx,ty);
    newbordering.add(r);
    while (!this.newbordering.isEmpty()) {
      r=this.newbordering.remove(0);
      int j=this.bordering(r);
      if (j==0 || j==4) continue;
      if (this.bordering_set.contains(r)) continue;
      this.bordering_set.add(r);
      bx=r.bx;by=r.by;tx=r.tx;ty=r.ty;
      // Get surrounding boxes.
      // 2,2:4,4
      // 0,0:2,2 -,-,-,-
      this.newbordering.add(new Rectangle(bx-gridXWidth,by-gridYWidth,tx-gridXWidth,ty-gridYWidth));
      // 2,0:4,2 *,-,*,-
      this.newbordering.add(new Rectangle(bx           ,by-gridYWidth,tx           ,ty-gridYWidth));
      // 4,0:6,2 +,-,+,-
      this.newbordering.add(new Rectangle(bx+gridXWidth,by-gridYWidth,tx+gridXWidth,ty-gridYWidth));
      // 4,2:6,4 +,*,+,*
      this.newbordering.add(new Rectangle(bx+gridXWidth,by           ,tx+gridXWidth,ty           ));
      // 4,4:6,6 +,+,+,+
      this.newbordering.add(new Rectangle(bx+gridXWidth,by+gridYWidth,tx+gridXWidth,ty+gridYWidth));
      // 2,4:4,6 *,+,*,+
      this.newbordering.add(new Rectangle(bx           ,by+gridYWidth,tx           ,ty+gridYWidth));
      // 0,4:2,6 -,+,-,+
      this.newbordering.add(new Rectangle(bx-gridXWidth,by+gridYWidth,tx-gridXWidth,ty+gridYWidth));
      // 0,2:2,4 -,*,-,*
      this.newbordering.add(new Rectangle(bx-gridXWidth,by           ,tx-gridXWidth,ty           ));
    }
  }
  // Tile the polygon by finding the largest rectanges within the polygon.
  // 1/ Find all bordering tiles with right tiles within the polygon.
  // 2/ Grow the tile to include the right hand tiles upto the right hand side.
  // 3/ Mark as deleted the right hand side.
  // 4/ Mark as deleted the right hand side.
  public void tiles(double gridXWidth, double gridYWidth) {
    Set<Rectangle> newBordering_set=new TreeSet<Rectangle>();
    Set<Rectangle> merged=new TreeSet<Rectangle>();
    for (Rectangle r:this.bordering_set) {
      if (merged.contains(r)) continue; // Already processed.
      Rectangle rightHandSide=new Rectangle(r);
      rightHandSide.moveX(gridXWidth);
      if (this.bordering(rightHandSide)==0) continue; // Right neighbour must be within the polygon.
      while (!this.bordering_set.contains(rightHandSide)) {
// todo : sanity check that X not larger than biggest X.
        rightHandSide.moveX(gridXWidth);
      }
      merged.add(rightHandSide);
      r.expand(rightHandSide);
      newBordering_set.add(r);
    }
    this.bordering_set=newBordering_set;

    boolean merged_b=true;
    ArrayList<Rectangle> fragments=new ArrayList<Rectangle>();
    while (merged_b) {
      merged_b=false;
      newBordering_set=new TreeSet<Rectangle>();
      Rectangle prev=null;
      for (Rectangle r:this.bordering_set) {
        if (prev==null) {
          prev=r;
          continue;
        }
        Rectangle newR=r.merge(prev,fragments);
        if (newR==null) {
          newBordering_set.add(prev);
          prev=r;
          continue;
        }
        for (Rectangle f:fragments) newBordering_set.add(f);
        fragments.clear();
        merged_b=true;
        prev=newR;
      }
      if (prev!=null) newBordering_set.add(prev);
      this.bordering_set=newBordering_set;
    }
  }
  public static void main(String[] args) {
    try {
      Path file=Paths.get("polygons.txt");
      BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
      String line=in.readLine();
      in.close();
      Polygon p=new Polygon(line);
      p.print();
      file=Paths.get("in.txt");
      in=new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
      while ((line = in.readLine()) !=null ) {
        String[] a=line.split(",");
        double x,y;
        x=Double.parseDouble(a[0]);
        y=Double.parseDouble(a[1]);
        boolean b=p.outside(x,y);
        System.out.println("in x="+x+" y="+y+" outside "+b);
      }
      in.close();
      p.tiles(0.00001,0.00001);
    } catch(Exception e) {
      e.printStackTrace();
      return;
    }
  }
}
