import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Dados{
    private ArrayList<Dados> totalDados;
    private int linhas;
    private int colunas;
    private String conteudo;
    private char[][] mapa;
    private Queue<Portos> portos;
    private ArrayList<Portos> marcados;

    public Dados(){
        totalDados = new ArrayList<>();
        portos = new LinkedList<>();
        marcados = new ArrayList<>();
    }

    public Dados(String conteudo){
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public char[][] getMapa() {
        return mapa;
    }

    public ArrayList<Dados> getTotalDados() {
        return totalDados;
    }


    public int getLinhas(){
        return linhas;
    }

    public int getColunas(){
        return colunas;
    }

    public boolean carregarDados(String nomeArquivo){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo),"ISO-8859-1"));
            String linha = "";
            String status = br.readLine();
            linhas = Integer.parseInt(status.split("[->: ]")[0]);
            colunas = Integer.parseInt(status.split("[->: ]")[1]);
            while((linha = br.readLine()) != null){
                linha = Normalizer.normalize(linha, Normalizer.Form.NFKD);
                linha = linha.replaceAll("[^\\p{ASCII}]", "");
                Dados d1 = new Dados(linha);
                totalDados.add(d1);
            }
            br.close();
        }
        catch(IOException e){
            return false;
        }
        return true;
    }

    public boolean criarMapa(){
        mapa = new char[linhas][colunas];
        Portos[] p = new Portos[10];
        for(int i = 0; i< linhas; i++){
            String linha = totalDados.get(i).conteudo;
            for(int j = 0; j<colunas;j++){
                mapa[i][j] = linha.charAt(j);
                if(!(linha.charAt(j) == '.') && !(linha.charAt(j) == '*')) {
                    p[Character.getNumericValue(linha.charAt(j))] = new Portos(Character.getNumericValue(linha.charAt(j)), i, j);
                }
            }
        }
        portos.addAll(Arrays.asList(p).subList(1, p.length));
        return true;
    }

    public void navegar(){
        marcados.add(portos.poll());
        int combustivelTotal = 0;

        while(!portos.isEmpty()){
            int combustivel = dijkstra(marcados.get(marcados.size()-1).getX(), marcados.get(marcados.size()-1).getY());
            if(combustivel != Integer.MAX_VALUE){
                combustivelTotal += combustivel;
                System.out.println("Distancia do porto: " + marcados.get(marcados.size()-1).getIndex() + " até o porto: " + portos.element().getIndex() + ": " + combustivel + " de combustivel");
                marcados.add(portos.poll());
            } else {
                System.out.println("Distancia do porto: " + marcados.get(marcados.size()-1).getIndex() + " até o porto: " + portos.element().getIndex() + ": não encontrado");
                portos.poll();
            }
        }
        portos.add(marcados.get(0));
        int combustivel = dijkstra(marcados.get(marcados.size()-1).getX(), marcados.get(marcados.size()-1).getY());
        combustivelTotal += combustivel;
        System.out.println("Distancia do porto: " + marcados.get(marcados.size()-1).getIndex() + " até o porto: " + portos.element().getIndex() + ": " + combustivel + " de combustivel");
        System.out.println("Combustivel total: " + combustivelTotal);
    }

    public int dijkstra(int origemX, int origemY) {
        int[][] dist = new int[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }
        dist[origemX][origemY] = 0;

        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{origemX, origemY, 0});

        while (!q.isEmpty()) {
            int[] val = q.poll();
            int x = val[0];
            int y = val[1];
            int distancia = val[2];

            if (Character.getNumericValue(mapa[x][y]) == portos.element().getIndex()) { 
                return distancia;
            }

            int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] direcao : direcoes) {
                int coordX = x + direcao[0];
                int coordY = y + direcao[1];

                if (coordX < 0 || coordX >= linhas || coordY < 0 || coordY >= colunas) {
                }
                else if(!(mapa[x][y] == '*')){
                    int novaDistancia = distancia + 1;
                    if (novaDistancia < dist[coordX][coordY]) {
                        dist[coordX][coordY] = novaDistancia;
                        q.offer(new int[]{coordX, coordY, novaDistancia});
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}