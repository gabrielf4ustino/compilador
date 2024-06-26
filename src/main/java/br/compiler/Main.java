package br.compiler;

import br.compiler.language.Token;
import br.compiler.lexicalanalyzer.Lexer;
import br.compiler.syntacticanalyzer.Parser;
import br.compiler.syntacticanalyzer.Sym;
import java_cup.runtime.Symbol;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Clear terminal
        clearTerminal();
        // Start a new prompt scanner
        Scanner scan = new Scanner(System.in);
        String rootPath = Paths.get("").toAbsolutePath().toString();
        System.out.print(rootPath + "> ");
        // Read a word in prompt
        String input = scan.next();
        // If input == quit end the program
        while (!Objects.equals(input, "quit")) {
            if (Objects.equals(input, "compile")) {
                input = scan.next();
                if (Objects.equals(input, "--help")) {
                    System.out.println("""
                            commands: compile | cat | quit | cls | clear
                            usage: compile [-l  | --lexical-analysis [<file>] [-o <name>]]
                                           [-s  | --syntactic-analysis [<file>]]
                                           [-gl | --generate-lexical-analysis [<file>]]
                                           [-gs | --generate-syntactic-analysis [<file>]]
                                   cat [<file>]       cls | clear""");
                    System.out.print(rootPath + "> ");
                    input = scan.next();
                } else if ((Objects.equals(input, "-l") || Objects.equals(input, "--lexical-analysis"))) {
                    try {
                        input = scan.next();
                        // Start the scanner analyzer with the file passed as parameter
                        Lexer scanner = new Lexer(new FileReader(input));
                        Symbol token = scanner.next_token();
                        input = scan.next();
                        if (!Objects.equals(input, "-o")) {
                            throw new RuntimeException();
                        }
                        input = scan.next();
                        // Make the path "result" if it does not exist
                        File theDir = new File(rootPath + "/src/main/java/br/compiler/result");
                        if (!theDir.exists()) {
                            theDir.mkdirs();
                        }
                        // Making the result file with the name passed as parameter
                        FileOutputStream outputStream = new FileOutputStream(rootPath + "/src/main/java/br/compiler/result/" + input + ".txt");
                        // Writing result in file
                        while (token.sym != Sym.EOF) {
                            Token tokenObj = (Token) token.value;
                            String value;
                            if (!Objects.equals(tokenObj.value, " ")) value = ", " + "\"" + tokenObj.value + "\"";
                            else value = "";
                            String output = ("<" + tokenObj.line + ":" + tokenObj.column + " " + tokenObj.name + value + ">\n");
                            outputStream.write(output.getBytes());
                            token = scanner.next_token();
                        }
                        outputStream.close();
                        System.out.println("done.");
                        System.out.print(rootPath + "> ");
                        input = scan.next();
                    } catch (FileNotFoundException e) {
                        System.out.println("compile: file '" + input + "' not found.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (IOException e) {
                        System.out.println("error: " + e.getMessage());
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (RuntimeException e) {
                        System.out.println("compile: '" + input + "' is not a compile command. See 'compile --help'.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    }
                } else if ((Objects.equals(input, "-s") || Objects.equals(input, "--syntactic-analysis"))) {
                    try {
                        input = scan.next();
                        // Start the parse analyzer with the file passed as parameter
                        Parser parser = new Parser(new Lexer(new FileReader(input)));
                        parser.parse();
                        System.out.println("Syntactically correct program.");
                        System.out.print(rootPath + "> ");
                        input = scan.next();
                    } catch (FileNotFoundException e) {
                        System.out.println("compile: file '" + input + "' not found.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (IOException e) {
                        System.out.println("error: " + e.getMessage());
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (RuntimeException e) {
                        System.out.println(e);
                        System.out.println("compile: '" + input + "' is not a compile command. See 'compile --help'.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    }
                } else if (Objects.equals(input, "-gl") || Objects.equals(input, "--generate-lexical-analysis")) {
                    try {
                        input = scan.next();
                        clearTerminal();
                        System.out.println("Gerando analizador léxico...");
                        // Generating a new lexical analyzer
                        if (generateLexer(rootPath, input)) {
                            rerunApp();
                            System.out.print(rootPath + "> ");
                            input = scan.next();
                        } else {
                            throw new FileNotFoundException();
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("compile: file '" + input + "' not found.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (IOException | InterruptedException e) {
                        System.out.println("error: " + e.getMessage());
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    }
                } else if (Objects.equals(input, "-gs") || Objects.equals(input, "--generate-syntactic-analysis")) {
                    try {
                        input = scan.next();
                        clearTerminal();
                        System.out.println("Gerando analizador sintático...");
                        // Generating a new lexical analyzer
                        if (generateParser(rootPath, input)) {
                            rerunApp();
                            System.out.print(rootPath + "> ");
                            input = scan.next();
                        } else {
                            throw new FileNotFoundException();
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("compile: file '" + input + "' not found.");
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    } catch (IOException | InterruptedException e) {
                        System.out.println("error: " + e.getMessage());
                        System.out.print(rootPath + "> ");
                        if (scan.hasNext()) {
                            scan.nextLine();
                        }
                        input = scan.next();
                    }
                } else {
                    System.out.println("compile: '" + input + "' is not a compile command. See 'compile --help'.");
                    System.out.print(rootPath + "> ");
                    input = scan.next();
                }
            } else if (Objects.equals(input, "cls") || Objects.equals(input, "clear")) {
                clearTerminal();
                System.out.print(rootPath + "> ");
                input = scan.next();
            } else if (Objects.equals(input, "cat")) {
                try {
                    input = scan.next();
                    // Reading the result file
                    FileReader file = new FileReader(rootPath + "/src/main/java/br/compiler/result/" + input + ".txt");
                    BufferedReader fileReader = new BufferedReader(file);
                    String line = fileReader.readLine();
                    // Printing the result file in prompt
                    while (line != null) {
                        System.out.println(line);
                        line = fileReader.readLine();
                    }
                    file.close();
                    System.out.print(rootPath + "> ");
                    input = scan.next();
                } catch (IOException e) {
                    System.out.println("error: " + e.getMessage());
                    System.out.print(rootPath + "> ");
                    if (scan.hasNext()) {
                        scan.nextLine();
                    }
                    input = scan.next();
                }
            } else {
                System.out.println("compile: '" + input + "' is not a compile command. See 'compile --help'.");
                System.out.print(rootPath + "> ");
                input = scan.next();
            }
        }
    }

    // Function that's run a clear command in terminal
    private static void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                Process process = Runtime.getRuntime().exec("clear");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (InterruptedException | IOException ignored) {
        }
    }

    // Function that's rerun a mvn install and the application
    private static void rerunApp() throws IOException, InterruptedException {
        if (System.getProperty("os.name").contains("Windows"))
            //rerun the program with the new lexical analyzer (in windows)
            new ProcessBuilder("cmd", "/c", "mvn install &&  java -jar ./target/compiler-v1.2.3-shaded.jar").inheritIO().start().waitFor();
        else {
            new ProcessBuilder("/bin/bash", "-c", "mvn install &&  java -jar ./target/compiler-v1.2.3-shaded.jar").inheritIO().start().waitFor();
        }
    }

    // Function that's run in terminal the build command of lexer analyzer
    private static boolean generateLexer(String rootPath, String fileName) throws IOException, InterruptedException {
        if (System.getProperty("os.name").contains("Windows")) {
            //rerun the program with the new lexical analyzer (in windows)
            new ProcessBuilder("cmd", "/c", "java -jar " + rootPath + "\\src\\main\\java\\br\\compiler\\lexicalanalyzer\\jflex-full-1.8.2.jar " + fileName).inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("/bin/bash", "-c", "java -jar " + rootPath + "/src/main/java/br/compiler/lexicalanalyzer/jflex-full-1.8.2.jar " + fileName).inheritIO().start().waitFor();
        }
        return true;
    }

    // Function that's run in terminal the build command of parser analyzer
    private static boolean generateParser(String rootPath, String fileName) throws IOException, InterruptedException {
        if (System.getProperty("os.name").contains("Windows")) {
            //rerun the program with the new lexical analyzer (in windows)
            new ProcessBuilder("cmd", "/c", "java -jar " + rootPath + "\\src\\main\\java\\br\\compiler\\syntacticanalyzer\\java-cup-11b.jar -parser Parser -symbols Sym -destdir " + rootPath + "\\src\\main\\java\\br\\compiler\\syntacticanalyzer\\ " + fileName).inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("/bin/bash", "-c", "java -jar " + rootPath + "/src/main/java/br/compiler/syntacticanalyzer/java-cup-11b.jar -parser Parser -symbols Sym -destdir " + rootPath + "/src/main/java/br/compiler/syntacticanalyzer/ " + fileName).inheritIO().start().waitFor();
        }
        return true;
    }
}
