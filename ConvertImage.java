import java.io.*;
import java.awt.*;
import javax.imageio.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import java.awt.geom.Rectangle2D;



class Param{
  
  static String get(String name,String defaultvalue){
    String value=System.getProperty(name);
    if(value==null) value=defaultvalue;
    System.err.printf("-D%s=\"%s\"%n",name,value);
    return value;
  };
  static String get(String name){
    return get(name,"");
  };

};

class MyImage{
  BufferedImage image=null;
  String filename=null;
  int w = 0;
  int h = 0;
  int x = 0;
  int y = 0;
  int PageColor=0;
  //int TextColor=0;
  
  MyImage(/*String filename*/)/*throws IOException*/{
    /*this.load(filename);*/
  };

  public MyImage load(String filename) throws IOException{
    try{
    this.filename=filename;
    File file= new File(filename);
    this.image = ImageIO.read(file);
    this.w = image.getWidth();
    this.h = image.getHeight();
    this.PageColor=image.getRGB(this.x,this.y);
    }catch(IOException e){ e.printStackTrace(System.err);}
    return this;
  }
  
  public void save(String filename) throws IOException{try{
    ImageIO.write(this.image,"png", new File(filename));
    
  }catch(IOException e){ e.printStackTrace(System.err);}}

  public void saveBlockOfImage(int x ,int y,int w,int h, String filename) throws IOException{try{
    ImageIcon imageIn = new ImageIcon(this.filename);
    BufferedImage imageOut = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB/*this.image.getType()/*,this.image.getColorModel()*/);
    Graphics2D g2d = (Graphics2D) imageOut.getGraphics();
    g2d.drawImage(imageIn.getImage(), -x, -y, null);
    g2d.dispose();
    //imageOut.getGraphics().drawImage(this.image, x, y, null);                                                           
    File file= new File(filename);
    ImageIO.write(imageOut,"png", file);
/*/
      final String filenameIn = "screencapture.jpg";
      final String filenameOut = "screencapture2.jpg";

      ImageIcon image = new ImageIcon(filenameIn);
      BufferedImage bufferedImage = new BufferedImage(
        100//image.getIconWidth()
        ,150// image.getIconHeight()
        ,BufferedImage.TYPE_INT_RGB
      );
      //Rectangle captureRect = new Rectangle(0, 0, 100, 150);

      Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
      g2d.drawImage(image.getImage(), -100, -110, null);
      g2d.dispose();
      
      OutputStream out = new PrintStream(filenameOut);
      ImageIO.write(bufferedImage, "jpg", out);
      out.close();

//*/
  }catch(IOException e){ e.printStackTrace(System.err);}}
  
  public int getPixel(int x,int y){
    //System.out.printf("--x=%5d, y=%5d, c=%x\n",x,y,image.getRGB(this.x,this.y));
    return this.PageColor!=image.getRGB(x,y) ? 1 : 0;
  }

  public int countPixels(int x ,int y,int w,int h){
    int c=0;
    for(int _x=x; _x<x+w; _x++)
      for(int _y=y; _y<y+h; _y++)
        c+=this.getPixel(_x,_y);
    return c;
  }
  public int getY_OfText(int x ,int y,int w,int h){
    int c=0;
    int i=y;
    for(; i<h ;i++){
      c=this.countPixels(x,i,w,1);
      if(c>0) break;
    }
    return i;
  }
  public int getH_OfText(int x ,int y,int w,int h){
    int _h=0;
    int c=~0;
    int i=y;
    for(; i<h && c!=0 ;i++){
      c=this.countPixels(x,i,w,1);
      if(c>0) _h++;
      else break;
    }
    return _h;
  }

  public int getX_OfText(int x ,int y,int w,int h){
    int c=0;
    int i=x;
    for(; i<w ;i++){
      c=this.countPixels(i,y,1,h);
      if(c>0) break;
    }
    return i;
  }
  public int getW_OfText(int x ,int y,int w,int h){
    int _w=0;
    int c;
    int i=x;
    for(; i<w ;i++){
      c=this.countPixels(i,y,1,h);
      if(c>0) _w++;
      else break;
    }
    return _w;
  }
  

};

class MyHtml{

  public static void header(String filename){
    String head=""
      +"<html>\n"
      +"<head>\n"
      +"<style>\n"
      +"  body {font-size:0px;margin:0;padding:0;background-color:#EEEEEE;}\n"
      +"  b{background-image:url(%s); border:0 solid lightgrey;}\n"
      +"  s{background-color:#FFFFEE; border:0 solid red;}\n"
      +"</style>\n"
      +"</head>\n"
      +"<body>\n"
    ;
    System.out.printf(head,filename);
  };

  public static void text(int x ,int y,int w,int h,MyImage im) throws IOException{
    String filename="img-x"+((Integer)x).toString()+"y"+((Integer)y).toString()+"w"+((Integer)w).toString()+"h"+((Integer)h).toString()+".png";
    System.out.printf("\n<img src=\"%s\"/>",filename);
    im.saveBlockOfImage(x,y,w,h,filename);
  };

  public static void space(int x ,int y,int w,int h){
    if(w<=0) return;
    System.out.printf("\n<s style=\"width:%d;height:%d;\"></s>",w,h);
  };

  public static void br(){
    System.out.printf("\n<br/>");
  };
  public static void tail(){
    System.out.printf("\n</body></html>");
  };

};


//*/
public class ConvertImage{

  public static void main(String args[])/* throws IOException*/{
    try{
    String in =Param.get("in","in.png");
    //String out=Param.get("out","out.png");
    MyHtml.header(in);
    MyImage im=new MyImage().load(in);
    int LIMIT=400;
    int Y=0,H=0,Ycnt=0;
    for(int i=0; i<LIMIT;i++){
      Y=im.getY_OfText(0,Y,im.w,im.h );
      H=im.getH_OfText(0,Y,im.w,im.h );
      if(0==H) break;
      
      //{System.out.printf("%3d Y=%5d, H=%5d\n",i,Y,H);}
         
      { 
        int XX,X=0,W=0,Xcnt=0;
        for(int j=0; j<LIMIT;j++){
          XX=X;
          X=im.getX_OfText(X,Y,im.w,H );
          W=im.getW_OfText(X,Y,im.w,H );
          if(0==W) break;
          //{System.out.printf("\t\t\t%3d X=%5d, W=%5d\n",j,X,W);}
          //MyHtml.text(XX,Y,W+(X-XX)+1,H+1);//space+text
          //MyHtml.text(XX,Y,(X-XX)+1,H+1);//space only
          MyHtml.space(XX,Y,(X-XX),H+0);//space only
          //MyHtml.text(X,Y,W+1,H+1);//text only
          MyHtml.text(X,Y,W+0,H+0,im);//text only
        
          {X+=W;}
        }
        MyHtml.br();
      }
         
      {Y+=H;}
    }
    MyHtml.tail();
    }catch(IOException e){;}
  }
};