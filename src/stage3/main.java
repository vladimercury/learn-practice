package stage3;

import java.io.*;
import java.net.URL;

/**
 * Created by dtgf on 11.02.2016.
 */

class htmlSymbolsInfo{
    public int htmlSymbols;
    public int allSymbols;

    public void append( htmlSymbolsInfo value ){
        this.htmlSymbols += value.htmlSymbols;
        this.allSymbols += value.allSymbols;
    }

    public double getRatio(){
        return this.htmlSymbols * 100.0 / this.allSymbols;
    }
}

class htmlSymbolsCounter{
    private String[] fileList;

    htmlSymbolsCounter( String[] fileNames ){
        fileList = fileNames.clone();
    }

    public void count(){
        for( String fileName : this.fileList ){
            try{
                BufferedReader fileInput;
                if( fileName.substring(0,7).equals("http://") )
                    fileInput = new BufferedReader(new InputStreamReader(new URL(fileName).openStream()));
                else
                    fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

                htmlSymbolsInfo fileHTMLSymbolsInfo = new htmlSymbolsInfo();

                while( fileInput.ready() )
                    fileHTMLSymbolsInfo.append(countSymbolsAtString(fileInput.readLine()));

                System.out.format("%s: coded symbols: %d (%.1f%%)\n", fileName, fileHTMLSymbolsInfo.htmlSymbols, fileHTMLSymbolsInfo.getRatio());
            }
            catch( IOException exception ){
                System.out.format("%s: file not found\n", fileName);
            }
        }
    }

    private static htmlSymbolsInfo countSymbolsAtString( String str ) {
        htmlSymbolsInfo countStatus = new htmlSymbolsInfo();
        int htmlSymbolStart = 0;
        boolean htmlIndexCheck = false;

        for (int index = 0; index < str.length(); index++) {
            switch (str.charAt(index)) {
                case '&': // html symbol start
                    htmlSymbolStart = index;
                    htmlIndexCheck = true;
                    break;
                case ';': // html symbol end
                    if (htmlIndexCheck) {
                        countStatus.htmlSymbols++;
                        countStatus.allSymbols -= index - htmlSymbolStart;
                    }
                    break;
            }
            countStatus.allSymbols++;
        }

        return countStatus;
    }
}

class keyCatcher implements Runnable{
    public void run(){
        try{
            System.in.read();
        }
        catch( IOException exc ){}

        System.exit(0);
    }
}

public class main {
    public static void main(String[] args) throws IOException {
        if( args.length == 0 ) return; // No arguments

        keyCatcher stopper = new keyCatcher();
        htmlSymbolsCounter counter = new htmlSymbolsCounter(args[0].split(";"));
        Thread thread = new Thread(stopper);
        thread.start();
        counter.count();
        System.exit(0);
    }
}
