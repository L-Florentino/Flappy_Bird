package com.projetoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Jogo extends ApplicationAdapter {

    //Texturas
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;

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

    @Override
    public void create() {
        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {
        verificarEstadoDoJogo();
        desenharTexturas();
    }

    private void verificarEstadoDoJogo(){

        //Movimentar o cano
        posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
        if (posicaoCanoHorizontal < - canoTopo.getWidth()){
            posicaoCanoHorizontal = larguraDispositivo;
            posicaoCanoVertical = random.nextInt(400) - 200;
        }

        // Aplicar evento de toque na tela
        boolean toqueTela = Gdx.input.justTouched();
        if (toqueTela){
            gravidade = -25;
        }
        // Aplicar a gravidade no pássaro
        if (posicaoPassaroY > 0 || toqueTela)
            posicaoPassaroY = posicaoPassaroY - gravidade;

        variacao += Gdx.graphics.getDeltaTime() * 10;
        // Verifica variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;

        gravidade ++;

    }

    private  void desenharTexturas(){
        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw( passaros[(int) variacao], 30, posicaoPassaroY);
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 +  posicaoCanoVertical);

        batch.end();
    }

    private  void inicializarTexturas(){
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");

    }

    private void inicializarObjetos(){

        batch = new SpriteBatch();
        random = new Random();

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoPassaroY = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 400;
    }

    @Override
    public void dispose() {
        Gdx.app.log("dispose", "Descarte de conteúdos");
    }
}
