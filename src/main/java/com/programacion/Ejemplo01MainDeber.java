package com.programacion;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Ejemplo01MainDeber {
    private static long ventana;
    private static int idTextura;

    static void ejecutar() {
        System.out.println("LWJGL " + Version.getVersion() + "!");
        inicializar();
        bucle();

        // Liberar recursos y terminar GLFW
        glfwFreeCallbacks(ventana);
        glfwDestroyWindow(ventana);
        glfwTerminate();
    }

    private static void inicializar() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) // Inicializar GLFW y lanzar error si hay alguna falla
            throw new IllegalStateException("No se pudo inicializar GLFW");

        // Configurar GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Crear la ventana
        int ancho = 300;
        int alto = 300;
        ventana = glfwCreateWindow(ancho, alto, "Deber01 - Juan Estrella", NULL, NULL);
        if (ventana == NULL)
            throw new RuntimeException("No se pudo crear la ventana GLFW");

        glfwSetKeyCallback(ventana, (ventana, tecla, escaneo, accion, mods) -> {
            if (tecla == GLFW_KEY_ESCAPE && accion == GLFW_RELEASE)
                glfwSetWindowShouldClose(ventana, true);
        });

        glfwSetFramebufferSizeCallback(ventana, (ventana, anchoVentana, altoVentana) -> {
            glViewport(0, 0, anchoVentana, altoVentana);
        });

        GLFWVidMode modoVideo = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(ventana, (modoVideo.width() - ancho) / 2, (modoVideo.height() - alto) / 2);

        glfwMakeContextCurrent(ventana);
        GL.createCapabilities();

        System.out.println("Version de OpenGL: " + glGetString(GL_VERSION));
        System.out.println("Proveedor de OpenGL: " + glGetString(GL_VENDOR));
        System.out.println("Renderizador de OpenGL: " + glGetString(GL_RENDERER));

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-1, 1, -1, 1, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glfwSwapInterval(1);

        idTextura = cargarTextura("madera.jpg");

        glEnable(GL_TEXTURE_2D);
        glfwShowWindow(ventana);
    }

    private static int cargarTextura(String ruta) {
        int idTextura;

        try (MemoryStack pilaMemoria = MemoryStack.stackPush()) {
            IntBuffer ancho = pilaMemoria.mallocInt(1);
            IntBuffer alto = pilaMemoria.mallocInt(1);
            IntBuffer canales = pilaMemoria.mallocInt(1);

            // Usar ClassLoader para obtener el recurso
            URL urlRecurso = Ejemplo01MainDeber.class.getClassLoader().getResource(ruta);
            if (urlRecurso == null) {
                throw new RuntimeException("No se pudo encontrar la textura: " + ruta);
            }

            String rutaRecurso = Paths.get(urlRecurso.toURI()).toString();

            System.out.println("Ruta de textura:" + rutaRecurso);

            ByteBuffer imagen = stbi_load(rutaRecurso, ancho, alto, canales, 4);
            if (imagen == null) {
                throw new RuntimeException("No se pudo cargar la textura: " + stbi_failure_reason());
            }

            idTextura = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, idTextura);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, ancho.get(), alto.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imagen);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            stbi_image_free(imagen);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Textura cargada con ID (cargarTextura): " + idTextura);

        return idTextura;
    }

    private static void bucle() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(ventana)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            pintarCuadrado();

            glfwSwapBuffers(ventana);
            glfwPollEvents();
        }
    }

    private static void pintarCuadrado() {
        glBindTexture(GL_TEXTURE_2D, idTextura);
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(-1.0f, -1.0f);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(-1.0f, 1.0f);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(1.0f, 1.0f);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(1.0f, -1.0f);
        glEnd();
    }

    public static void main(String[] args) {
        ejecutar();
    }
}
