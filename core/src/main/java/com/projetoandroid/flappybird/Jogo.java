package com.projetoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Jogo extends ApplicationAdapter {

    //Texturas
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    // Formas para colisão
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;

    // Atributos de configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float gravidade = 1;
    private float posicaoPassaroY = 0;
    private float posicaoCanoHorizontal = 0;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;
    private Random random;
    private int pontos = 0;
    private boolean passouCano = false;
    private int estadoJogo = 0;

    // Exibição de textos
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciair;
    BitmapFont textoMelhorPontuacao;

    @Override
    public void create() {
        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {
        verificarEstadoDoJogo();
        validarPontos();
        desenharTexturas();
        detectarColisoes();
    }

    private void verificarEstadoDoJogo(){

        boolean toqueTela = Gdx.input.justTouched();

        if (estadoJogo == 0) {

            // Aplicar evento de toque na tela
            if (toqueTela){
                gravidade = -15;
                estadoJogo = 1;
            }

        } else if (estadoJogo == 1) {

            // Aplicar evento de toque na tela
            if (toqueTela){
                gravidade = -15;
            }

            //Movimentar o cano
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
            if (posicaoCanoHorizontal < - canoTopo.getWidth()){
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoVertical = random.nextInt(400) - 200;
                passouCano = false;
            }

            // Aplicar a gravidade no pássaro
            if (posicaoPassaroY > 0 || toqueTela)
                posicaoPassaroY = posicaoPassaroY - gravidade;

            gravidade ++;

        } else if (estadoJogo == 2) {

        }
    }

    private void detectarColisoes(){

        circuloPassaro.set(
            50 + passaros[0].getWidth() / 2, posicaoPassaroY + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2
        );

        retanguloCanoBaixo.set(
            posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
            canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        retanguloCanoTopo.set(
            posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical,
            canoTopo.getWidth(), canoTopo.getHeight()
        );

        boolean colidiuCanoTopo = Intersector.overlaps(circuloPassaro, retanguloCanoTopo);
        boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        if ( colidiuCanoTopo || colidiuCanoBaixo ){
            Gdx.app.log("Log", "Colidiu");
            estadoJogo = 2;
        }

        /*
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);

        shapeRenderer.circle(50 + passaros[0].getWidth() / 2, posicaoPassaroY + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        // Topo
        shapeRenderer.rect(
            posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical,
            canoTopo.getWidth(), canoTopo.getHeight()
        );

        // Baixo
        shapeRenderer.rect(
            posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
            canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        shapeRenderer.end();
        */
    }

    private  void desenharTexturas(){
        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw( passaros[(int) variacao], 50, posicaoPassaroY);
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical);
        textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 110);

        if (estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            textoReiniciair.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "Seu record é: 0 pontos", larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight());
        }

        batch.end();
    }

    public void validarPontos(){


        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            // Passou da posição do pássaro
            if (!passouCano) {
                pontos++;
                passouCano = true;
            }
        }

        variacao += Gdx.graphics.getDeltaTime() * 10;
        // Verifica variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;
    }


    private  void inicializarTexturas(){
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");

    }

    private void inicializarObjetos(){

        batch = new SpriteBatch();
        random = new Random();

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoPassaroY = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 400;

        // Configuração do texto
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoReiniciair = new BitmapFont();
        textoReiniciair.setColor(Color.GREEN);
        textoReiniciair.getData().setScale(2);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.RED);
        textoMelhorPontuacao.getData().setScale(2);

        // Formas geométricas para colisões
        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoBaixo = new Rectangle();
        retanguloCanoTopo = new Rectangle();
    }

    @Override
    public void dispose() {
        Gdx.app.log("dispose", "Descarte de conteúdos");
    }
}
