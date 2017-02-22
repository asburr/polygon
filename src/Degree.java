// Stores integer x and y degree coordinates.
// Doubles are converted to integer using the following function:
//   coordinateToInt(double d)
// Precision is to five decimal places.
// Tenth place digit is stored in a Degree, all the digits in a
// Degree are populated with data and the data is the same the
// Degree is deleted and the data reference is moved up to the 
// parent Degree. This is called a partial degree in that
// the Degree structure partially maps the degree.

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

class Degree {
  static final int ND=8; // 123.45678 is 8 number of digits (ND),
  static final int[] MV={ // Maximum value at each three-bit digit position.
         0,
         7, // Digit 1
         63, // Digit 2
         511, // Digit 3
         4095, // Digit 4
         32767, // Digit 5
         262143, // Digit 6
         2097151, // Digit 7
         16777215  // Digit 8
         };
  static final int[] BS={ // Bit Shift for each three-bit digit position.
        -1,
         0, // Digit 1
         3, // Digit 2
         6, // Digit 3
         9, // Digit 4
         12, // Digit 5
         15, // Digit 6
         18, // Digit 7
         21, // Digit 8
         24  // Digit 9
         };
  Degree next[][]=new Degree[Degree.ND][Degree.ND];
  String data[][]=new String[Degree.ND][Degree.ND];
  Degree parent;
  int parentx,parenty;
  Degree() { }
  // Return true when Degree has the same data item in all
  // 100 places.
  private boolean full() {
    String data=null;
    for (int x=0;x<Degree.ND;x++) {
      for (int y=0;y<Degree.ND;y++) {
        String d=this.data[x][y];
        if (d==null) return false;
        if (data==null) data=d;
        else if (!d.equals(data)) return false;
      }
    }
    return true;
  }
  private void clear() {
    for (int x=0;x<Degree.ND;x++) {
      for (int y=0;y<Degree.ND;y++) {
        this.data[x][y]=null;
        this.next[x][y]=null;
      }
    }
  }
  static int Multiple=100000; // Five decimal places.
  static int coordinateToInt(double d) { return (int)(d*Degree.Multiple); }
  static double coordinateToDouble(int i) { double d=i; return d/Degree.Multiple; }
  private Degree checkfull() {
    if (this.parent==null) return this; // Dont collapse root Degree.
    if (this.full()) {
      this.parent.data[this.parentx][this.parenty]=this.data[0][0];
      this.parent.next[this.parentx][this.parenty]=null;
      this.clear();
      return this.parent.checkfull();
    }
    return this;
  }
  private void add(Degree parent, int parentx, int parenty, int xs,int ys,String data,int nd) {
    this.parent=parent;
    this.parentx=parentx;
    this.parenty=parenty;

    if (nd == 1) { // Last digit, store data.
      this.data[xs][ys]=data;
      this.checkfull();
      return;
    }
    int x=0,y=0, n=nd-1;
    if (xs > Degree.MV[n]) { x=xs>>Degree.BS[nd];xs=(xs&Degree.MV[n]); }
    if (ys > Degree.MV[n]) { y=ys>>Degree.BS[nd]; ys=(ys&Degree.MV[n]); }
    String d=this.data[x][y];
    if (d!=null) {
      if (!data.equals(d)) {
        System.err.println("x="+x+" y="+y+" hasData="+d+" conflictData="+data);
      }
      return;
    }
    if (this.next[x][y]==null) this.next[x][y]=new Degree();
    this.next[x][y].add(this,x,y,xs,ys,data,n);
  }
  public void add(int x,int y,String data) {
    if (x>18000000) { System.err.println("X "+x+" too large"); return; }
    if (y>18000000) { System.err.println("Y "+y+" too large"); return; }
    this.add(null,0,0,x,y,data,Degree.ND);
  }
  private String find(int xs, int ys,int nd) {
    int x=0,y=0,n=nd-1;
    if (xs > Degree.MV[n]) {
      x=xs>>Degree.BS[nd]; xs=(xs&Degree.MV[n]);
    }
    if (ys > Degree.MV[n]) {
      y=ys>>Degree.BS[nd]; ys=(ys&Degree.MV[n]);
    }
    String d=this.data[x][y];
    if (d!=null) return d;
    if (this.next==null) return null;
    Degree N=this.next[x][y];
    if (N!=null) return N.find(xs,ys,n);
    return null;
  }
  public String find(int x, int y) {
    return this.find(x,y,Degree.ND);
  }
  public void write(String file) throws IOException {
    BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
    this.write(bw,"");
    bw.close();
  }
  public void write(BufferedWriter out,String tab) throws IOException {
    out.write(tab+"{\n");
    for (int x=0;x<Degree.ND;x++) {
      for (int y=0;y<Degree.ND;y++) {
        String d=this.data[x][y];
        Degree n=this.next[x][y];
        if (d!=null) out.write(tab+x+","+y+","+d+"\n");
        if (n!=null) { out.write(tab+x+","+y+"\n"); n.write(out,tab+" "); }
      }
    }
    out.write(tab+"}\n");
  }
  public void print(int xs,int ys) {
    xs=(xs<<3); ys=(ys<<3);
    for (int x=0;x<Degree.ND;x++) {
      for (int y=0;y<Degree.ND;y++) {
        String d=this.data[x][y]; Degree n=this.next[x][y];
        if (d!=null) System.out.println((xs+x)+","+(ys+y)+","+d);
        if (n!=null) n.print(xs+x,ys+y);
      }
    }
  }
  public static Degree DegreeFactory(BufferedReader in,Map<String,String> datas,String tab) {
   Degree pdegree=new Degree();
   Degree degree=pdegree;
   try {
    int state=0;
    String line;
    while ((line = in.readLine()) !=null ) {
      if (state==0) {
        if (!line.equals(tab+"{")) {
          System.err.println("read: expecting \""+tab+"{\" got \""+line+"\"");
          return pdegree;
        }
        state=1;
        continue;
      }
      if (line.equals(tab+"}")) {
        state=0;
        continue;
      }
      String[] a=line.split(",",-1);
      int x=0,y=0;
      try {
        x=Integer.parseInt(a[0]);
        y=Integer.parseInt(a[1]);
      } catch (Exception e) {
        System.err.println("Failed to parse int x and y from \""+line+"\"");
        return pdegree;
      }
      if (a.length == 2) { // x,y
        if (degree.next[x][y]==null) {
          Degree nd=new Degree();
          nd.parent=degree;
          nd.parentx=x;
          nd.parenty=y;
          degree.next[x][y]=nd;
          degree=nd;
        } else degree=degree.next[x][y];
      } else { // x,y,data
        String newd=a[2];
        String d=datas.get(newd);
        if (d==null) { datas.put(newd,newd); d=newd; }
        degree.data[x][y]=d;
        if (degree.data[x][y]!=null) {
          System.err.println("x="+x+" y="+y+" hasData="+d+" conflictData="+degree.data[x][y]);
          return pdegree;
        }
        degree=degree.checkfull();
      }
    }
   } catch(Exception e) {
    e.printStackTrace();
   }
   return pdegree;
  }
  public static Degree DegreeFactory(BufferedReader in) {
    return Degree.DegreeFactory(in,new TreeMap<String,String>(),"");
  }
  public static Degree DegreeFactory(String file) throws IOException {
    BufferedReader in=new BufferedReader(new FileReader(file));
    Degree d=Degree.DegreeFactory(in);
    in.close();
    return d;
  }
  public static void main(String[] args) {
    Degree root=new Degree();
    root.add(1234,4321,"1234");
    root.add(2234,4322,"2234");
    root.add(2235,5322,"2235");
    root.print(0,0);
    try {
      root.write("Polygon_test.txt");
      Degree d=Degree.DegreeFactory("Polygon_test.txt");
      d.print(0,0);
    } catch (Exception e) { e.printStackTrace(); }
  }
}
