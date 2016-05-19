package br.ufc.engsoftware.tasabido;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.PriorityQueue;

import br.ufc.engsoftware.DAO.PerfilDAO;
import br.ufc.engsoftware.aux.GetServerDataAsync;
import br.ufc.engsoftware.aux.PostServerDataAsync;
import br.ufc.engsoftware.aux.Statics;
import br.ufc.engsoftware.models.Perfil;


public class PaginaPrincipalActivity extends FragmentActivity {
    JSONObject obj;
    ArrayList<Perfil> arrayPerfilList;
    /******************* A T E N Ç Ã O *******************/

    /* As alterações de cada página NÃO devem ser feitas aqui.
     * As alterações devem ser feitas nas classes e layouts
     * respectivos de cada FRAGMENT.
     *
     * Alterações na página de PERFIL devem ser feita na classe
     * PerfilFragment e no layout fragment_perfil
     *
     * * Alterações na página de GPS devem ser feita na classe
     * GpsFragment e no layout fragment_gps
     *
     * * Alterações na página de MATERIAS devem ser feita na classe
     * MateriaFragment e no layout fragment_materias
     *
     * Sabendo disso caso você faça alguma alteração NESTA classe
     * você deve ter um bom motivo :D
     */


    // Nome do arquivo de preferencias compartilhadas
    public static final String PREFERENCES_FILE_NAME = "TaSabidoSharedPreferences";

    // Número de páginas que devem ser criadas do ViewPager
    private static final int NUM_PAGES = 3;

    // Atributo que vai guardar o ViewPager da pagina inicial
    private ViewPager mPager;

    // PageAdapter do ViewPager
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        // Instancia o ViewPager e o PageAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Seta o TabLayout (abas) para trabalhar em conjunto com o ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mPager);

        Perfil perfil = new Perfil("Joaozim", "joazim@gmail.com", "123");

        listaUsuarios();
//        cadastrarUsuario(perfil);


        // Muda o layout das abas para colocar os ícones
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(((ScreenSlidePagerAdapter) mPagerAdapter).getTabView(i));
        }
    }

    public void listaUsuarios(){
        arrayPerfilList = new ArrayList<Perfil>();

        try {
            new GetServerDataAsync(new GetServerDataAsync.AsyncResponse(){

                @Override
                public void processFinish(String output){
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    try {
                        obj = new JSONObject(output);
                        JSONArray arraySupermarketJson = obj.getJSONArray("results");

                        arrayPerfilList = new ArrayList<Perfil>();
                        Perfil perfil;

                        for (int i = 0; i < arraySupermarketJson.length(); i++) {

                            JSONObject jo_inside = arraySupermarketJson.getJSONObject(i);

                            String nome_usuario = jo_inside.getString("nome_usuario");
                            String email = jo_inside.getString("email");

                            perfil = new Perfil(nome_usuario, email);

                            arrayPerfilList.add(perfil);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    supermarketAdapter = new SupermarketAdapter(getActivity(), arrayPerfilList);
//                    supermarketAdapter.notifyDataSetChanged();
//                    supermarkets.setAdapter(supermarketAdapter);
                }
            }).execute(Statics.LISTAR_USUARIOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cadastrarUsuario(Perfil perfil){
        PerfilDAO dao = new PerfilDAO();
        dao.add(perfil);
    }

    // Metodo que seta a funcionalidade do botão de voltar
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }



    //Classe do PageAdapater para inserir os fragments no ViewPager
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        // Atributo com os icones que vão ser usado nas abas do TabLayout
        private int[] imageResId = {
                R.drawable.book_icon_1 ,
                R.drawable.gps_icon_1,
                R.drawable.user_icon_1
        };

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Metodo que retorna os fragments que vão popular o ViewPager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MateriaFragment();
            } else if (position == 1) {
                return new GpsFragment();
            } else if (position == 2) {
                return new PerfilFragment();
            } else {
                return null;
            }

        }

        // Metodo que diz o numero de páginas que o ViewPager terá
        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        // Seta o titulo das paginas, apesar que é sobrescrito pelos ícones
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            CharSequence titles[] = {"Pagina 1", "Pagina 2", "Pagina 3"};
            return titles[position];
        }

        // Seta os ícones das abas
        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_layout, null);
            ImageView img = (ImageView) v.findViewById(R.id.imgView);
            img.setImageResource(imageResId[position]);

            return v;
        }

    }

}
