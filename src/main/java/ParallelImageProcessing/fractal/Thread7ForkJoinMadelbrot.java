package ParallelImageProcessing.fractal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.concurrent.ForkJoinPool;

class Thread7ForkJoinMadelbrot extends ApplicationAdapter {

    SpriteBatch batch;

    Texture image;

    Pixmap pixmap;

    Mandelbrot mandelbrot;

    Label label;

    ForkJoinPool forkJoinPool;

    long single = 0;

    long multi = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        pixmap = new Pixmap(128, 256, Pixmap.Format.RGB888);
        mandelbrot = new Mandelbrot(pixmap, 1);
        image = new Texture(pixmap);
        forkJoinPool = new ForkJoinPool(4);

        // Single thread generation
        long start = System.currentTimeMillis();
        mandelbrot.generate();
        long end = System.currentTimeMillis();
        single = end - start;
        image.draw(pixmap, 0, 0);

        // Fork/Join framework generation
        start = System.currentTimeMillis();
        final Pixmap mtPixmap = forkJoinPool.invoke(mandelbrot);
        end = System.currentTimeMillis();
        multi = end - start;
        image.draw(mtPixmap, 0, 0);

        Gdx.app.log("Calc", "Thread:" + Mandelbrot.threadCount + " single: " + single + " ms multi Fork/Join: " + multi + " ms");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(image, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}