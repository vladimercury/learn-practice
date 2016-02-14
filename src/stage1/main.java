package stage1;

import java.io.*;

/**
 * Created by dtgf on 11.02.2016.
 */

public class main {
    public static void main(String[] args) throws IOException {
        if( args.length == 0 ) return; // No arguments

        String[] textFileNameList = args[0].split(";");

        for( String fileName : textFileNameList ){
            try{
                BufferedReader fileInput = new BufferedReader(new InputStreamReader(new FileInputStream( fileName)));
                htmlSymbolsInfo fileHTMLSymbolsInfo = new htmlSymbolsInfo();

                while( fileInput.ready() )
                    fileHTMLSymbolsInfo.append(countHTMLSymbols(fileInput.readLine()));

                System.out.format("%s: coded symbols: %d (%.1f%%)\n", fileName, fileHTMLSymbolsInfo.htmlSymbols, fileHTMLSymbolsInfo.getRatio());
            }
            catch( FileNotFoundException exception ){
                System.out.format("%s: file not found\n", fileName);
            }
        }
    }

    private static class htmlSymbolsInfo{
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

    private static htmlSymbolsInfo countHTMLSymbols( String str ){
        htmlSymbolsInfo countStatus = new htmlSymbolsInfo();
        int htmlSymbolStart = 0;
        boolean htmlIndexCheck = false;

        for( int index = 0; index < str.length(); index++ ){
            switch ( str.charAt(index) ){
                case '&': // html symbol start
                    htmlSymbolStart = index;
                    htmlIndexCheck = true;
                    break;
                case ';': // html symbol end
                    if( htmlIndexCheck ){
                        countStatus.htmlSymbols++;
                        countStatus.allSymbols -= index - htmlSymbolStart;
                    }
                case ' ': // "& ;" is not an html symbol
                    htmlIndexCheck = false;
                    break;
            }
            countStatus.allSymbols++;
        }

        return countStatus;
    }
}
