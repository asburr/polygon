import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;   
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStream;  
import java.util.Vector;
import java.util.Set;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

public class Polygons {
  private Vector<Polygon> polygons=new Vector<Polygon>();
  Polygons(String filename) {
    Path file=Paths.get(filename);
    try {
      BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
      String line;
      while ((line = in.readLine()) !=null ) {
        if (!line.startsWith("#")) polygons.add(new Polygon(line));
      }
    } catch(Exception e) {
      e.printStackTrace();
      return;
    }
  }
  public void print() {
    for (Polygon polygon: this.polygons) {
      polygon.print();
    }
  }
  public void printX(int columns,int rows) {
    Rectangle rectangle=new Rectangle(0.0,0.0,1.0,1.0);
    for (Polygon polygon: this.polygons) rectangle.expand(polygon.rectangle);
    rectangle.print();
    double dx=(rectangle.tx-rectangle.bx)/(columns-1);
    double dy=(rectangle.ty-rectangle.by)/(rows-1);
    for (double y=rectangle.ty;y>=rectangle.by;y-=dy) {
      for (double x=rectangle.bx;x<=rectangle.tx;x+=dx) {
        String s=this.find(x,y);
        if (s!=null) System.out.print(s.substring(0,1));
        else System.out.print(".");
      }
      System.out.println("");
    }
  }
  public void printXBorder(int columns,int rows) {
    for (Polygon polygon: this.polygons) { 
      System.out.println(polygon.data);
      double dx=(polygon.rectangle.tx-polygon.rectangle.bx)/(columns-1);
      double dy=(polygon.rectangle.ty-polygon.rectangle.by)/(rows-1);
      polygon.border(dx,dy);
      Rectangle rectangle=new Rectangle(0.0,0.0,1.0,1.0);
      for (double y=polygon.rectangle.ty;y>=polygon.rectangle.by;y-=dy) {
        for (double x=polygon.rectangle.bx;x<=polygon.rectangle.tx;x+=dx) {
          rectangle.move(x,y,x,y);
          if (polygon.bordering_set.contains(rectangle)) System.out.print("*");
/*
          else if (polygon.bordering0.contains(rectangle)) System.out.print("0");
          else if (polygon.bordering1.contains(rectangle)) System.out.print("1");
          else if (polygon.bordering2.contains(rectangle)) System.out.print("2");
          else if (polygon.bordering3.contains(rectangle)) System.out.print("3");
          else if (polygon.bordering4.contains(rectangle)) System.out.print("4");
*/
          else System.out.print(".");
        }
        System.out.println("");
      }
      return;
    }
  }
  public void printXTiled(int columns,int rows) {
    for (Polygon polygon: this.polygons) { 
      System.out.println(polygon.data);
      double dx=(polygon.rectangle.tx-polygon.rectangle.bx)/(columns-1);
      double dy=(polygon.rectangle.ty-polygon.rectangle.by)/(rows-1);
      polygon.border(dx,dy);
      Rectangle rectangle=new Rectangle(0.0,0.0,1.0,1.0);
      for (double y=polygon.rectangle.ty;y>=polygon.rectangle.by;y-=dy) {
        for (double x=polygon.rectangle.bx;x<=polygon.rectangle.tx;x+=dx) {
          rectangle.move(x,y,x,y);
          if (polygon.bordering_set.contains(rectangle)) System.out.print("*");
          else System.out.print(".");
        }
        System.out.println("");
      }
      return;
    }
  }
  public String find(double testx, double testy) {
    for (Polygon polygon: this.polygons) {
      if (!polygon.outside(testx,testy)) {
        return polygon.data;
      }
    }
    return null;
  }
  private void test() {
    Path file=Paths.get("ins.txt");
    try {
      BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
      String line;
      while ((line = in.readLine()) !=null ) {
        String[] a=line.split(",");
        double x=Double.parseDouble(a[0]);
        double y=Double.parseDouble(a[1]);
        System.out.println("x="+x+" y="+y+" found "+this.find(x,y));
      }
    } catch(Exception e) {
      e.printStackTrace();
      return;
    }
  }
  public static void main(String[] args) {
    // Polygons p=new Polygons("polygons.txt");
    if (args.length != 3) {
      System.out.println("USAGE: $COLUMNS $LINES FILE");
      return;
    }
    Polygons p=new Polygons(args[2]);
    int columns=Integer.parseInt(args[0]);
    int rows=Integer.parseInt(args[1]);
    p.print();
    p.test();
    p.printX(columns,rows);
    p.printXBorder(columns,rows);
return;
/*
    p.print();
    Path file=Paths.get("out.txt");
    try {
      BufferedWriter out=new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file)));
      d.write(out);
    } catch (Exception e) { e.printStackTrace(); }
*/
  }
}
