package CS241;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
        public String content = "";
        public int pos = 0;

        public FileReader(String fileName){
            try {

                File myObj = new File(fileName);
                if (!myObj.exists()){
                    System.out.println("File missing!");
                }
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    this.content += data + " ";
//                    System.out.println(data);
                }
                myReader.close();
            }catch (FileNotFoundException e) {
//                System.out.println("File not found.");
//                e.printStackTrace();
                this.content = fileName;
            }
            this.content = this.content.strip();
            this.content += " §";

            System.out.println(this.content);

        } // constructor: open file

        public char GetNext(){
            if (this.pos == this.content.length()){
                return this.content.charAt(this.pos-1);
            }else {
                return this.content.charAt(this.pos++);
            }
        } // return current and advance to the next character on the input

        public void Error(String errorMsg){
            System.out.println(errorMsg + " at position " + this.pos);
        }; // signal an error message

//    public static void main(String[] args){
////        FileReader f = new FileReader("/Users/yurunsong/Desktop/Pre.txt");
//        FileReader f = new FileReader("main \n" + "var a, b, c, d, e; {");
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        f.GetNext();
//        System.out.println(f.GetNext());
//        f.Error("Not found");
//    }


}

