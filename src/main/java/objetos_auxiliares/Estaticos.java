package objetos_auxiliares;

public class Estaticos {
    //Objeto com varios atributos estaticos para persistir dados entre mudanças de telas
    private static long FRAGMENT_INICIO_DATA = 0;

    public static long getFragmentInicioData() {
        return FRAGMENT_INICIO_DATA;
    }

    public static void setFragmentInicioData(long fragmentInicioData) {
        FRAGMENT_INICIO_DATA = fragmentInicioData;
    }
}
