package com.fahadandroid.groupchat.helpers;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fahadandroid.groupchat.ApiCall.RetrofitAPIClient;
import com.fahadandroid.groupchat.models.CompanyApiModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.CountryModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EUGroupChat extends Application {
    public static List<UserModel> userModelList;
    List<String> userKeys, countryKeys;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, countriesRef, companiesRef;
    public static List<CompanyModel> barcelonaCompanyList, cataniaCompanyList;
    public static UserModel currentUser;
    public static List<CountryModel> countryModelList;
    public static List<CompanyApiModel> companiesList;
    FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        countriesRef = firebaseDatabase.getReference("countries");
        companiesRef = firebaseDatabase.getReference("companies");
        barcelonaCompanyList = new ArrayList<>();
        cataniaCompanyList = new ArrayList<>();
        getUsers();
        getCountryList();
        populateCompaniesLists();
    }
    private void getUsers(){
        userModelList = new ArrayList<>();
        userKeys = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null){
                        userModelList.add(userModel);
                        userKeys.add(userModel.getUid());
                        if (mAuth.getCurrentUser()!=null){
                            if (userModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                currentUser = userModel;
                            }
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null){
                        int index = userKeys.indexOf(userModel.getUid());
                        userModelList.set(index, userModel);
                        if (mAuth.getCurrentUser()!=null){
                            if (userModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                currentUser = userModel;
                            }
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null) {
                        int index = userKeys.indexOf(userModel.getUid());
                        userModelList.remove(index);
                        userKeys.remove(userModel.getUid());
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addChildEventListener(childEventListener);
    }
    private void getCountryList(){
        countryModelList = new ArrayList<>();
        countryKeys = new ArrayList<>();
        countriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    countryModelList.add(countryModel);
                    countryKeys.add(countryModel.getKey());
                }catch (Exception e){

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    int index = countryKeys.indexOf(countryModel.getKey());
                    countryModelList.set(index, countryModel);
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    int index = countryKeys.indexOf(snapshot.getKey());
                    countryModelList.remove(index);
                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void populateCompaniesLists(){
        CompanyModel catania1 = new CompanyModel("", "", "1+",
                "Piazza Lolanda 3/4", "95100", "Catania", "Sicily, Italy", "095 836 5921",
                "amministrazione@visodent.it", "", "Dott.ssa Russo Marta/Emanuela Tumminelli", "",
                "visodent.it", "It is involved in dental and beauty care.", "");
        CompanyModel catania2 = new CompanyModel("", "", "2",
                "Via Renato Focini, 1", "95126", "Catania", "Sicily, Italy", "095 836 5921",
                "", "", "Giuseppe Costanzo", "",
                "", "Dental Technician, Dental prothesis", "");
        cataniaCompanyList.add(catania1);
        cataniaCompanyList.add(catania2);

        CompanyModel barcelona1 = new CompanyModel("Azienda Agricole Pietrini Salvatore", "VETPRO",
                "via Marconi 151", "98051", "Barcellona P.G (Messina)", "Sicily, Italy", "(+39) 3927912211", "agribio.pietrini@gmail.com",
                "www.agribio-pietrini.it", "The food is prepared using traditional Italian recipes. The basis of flavors are found through naturally grown food, free of genetic modification and chemicals. Their production is consistent with the laws of nature. They prepare the dishes using only the best quality ingredients.",
                "Salvatore Pietrini", "During the visit the students will learn about Bio Agricolture from the Pietrini Family, owners of a biological Holiday Farm situated in the Peloritains mountains. This holiday farm is situated into 45 hectares of an open air museum, where it will be possible to learn more about classical mythology, history, philosophy, science, art and many other things; in a context that fascinates and rouses curiosity. It will be a great way to learn outside of the school walls. ");
        CompanyModel barcelona2 = new CompanyModel("Azienda Agricola Cambria", "VETPRO", "Contrada San Filippo", "98054", "Furnari (Messina)",
                "Sicily, Italy", "(+39) 0941840214", "info@cambriavini.com", "http://cambriavini.com/it", "The Cambria wine company produces very good wines from harvested indigenous grapes \"nocera\" from the estate of Mastronicola. It pursues a specific objective: to highlight the uniqueness of the area using the highest standards of win making thanks to a balance between modern technology and careful craftsmanship and tradition. ",
                "Nino Cambria", "During the visit students will have the opportunity to look closely at the wine production process in the traditional Sicilian winery. It will be a chance to see how wine is produced. They will also have a unique opportunity to talk with people who know almost everything about wine, its production and abroad commercialization. They will also see an exhibition which is composed of the best vintages produced in the winery. ");
        CompanyModel barcelona3 = new CompanyModel("La Vivaio Palmara", "VETPRO", "Palmara, 56", "98050", "Terme Vigliatore (Messina)",
                "Sicily, Italy", "(+39) 0941874192", "", "http://www.vivaiolapalmara.com/", "La Vivaio Palmar was the first company in Sicily engaged in the production of ornamental fruit trees and citrus. The company has one of the richest collections in the market. In their offer you can find many historical Italian varieties, including hybrids derived from varieties from around the world.",
                "The company belongs to three partner-friends, who share a strong passion for tradition and plants Carmelo Scilipoti, Salvuccio Materia e Andrea Maio.",
                "The visit is to the first company in Sicily which produced ornamental citrus fruit trees. Students will to see the historical Italian varieties of plants, including hybrids, with varieties from all over the world including exclusive hybrids. They will have an opportunity to see  the creation of new varieties of citrus fruit trees through the careful selection of unusual products from all over the world and also see the production and marketing of the whole collection on a large scale.");
        CompanyModel barcelona4 = new CompanyModel("Isgro Salvatore Piante", "2", "Via Sottochiesa 47", "98050", "Terme Vigliatore (Messina)",
                "Sicily, Italy", "(+39) 0909740661", "salvatore.isgro1@virgilio.it", "", "The company have a key position in the production of seedlings in the southern Italy. In its offer there are many kinds of fruit trees. It is also a major exporter of plants to other European countries.",
                "", "During the visit in the greenhouse students will be able to see the production and cultivation of ornamental and Mediterranean plants. They will see with their own eyes many species of trees, such as: ornamental trees, citrus lemon trees, plants and flowers, cedar, mandarin trees, ornamental plants, orange and grapefruit trees. They will also learn about the proper care of plants.");
        CompanyModel barcelona5 = new CompanyModel("Fratelli Branca s.p.a", "VETPRO", "Via Maceo, 7", "98050", "Terme Vigliatore", "Sicily, Italy",
                "(+39) 0909781048", "ketty@brancaspa.com", "http://www.brancaspa.com/", "Fratelli Branca is a privately owned family company. It was founded in 1892 exclusively as manufacturer of citrate and lemon essential oil obtained manually with the traditional \" sponge \" extraction method. Following the initial success, the company enlargens its activity to include orange and mandarin processing for the production of juice concentrates, diced peels and the corresponding essential oils. Over the last 20 years the company experiences a tremendous growth thanks to continous technoligical innovations and targeted investments, thus reaching the current dimensions which place the company in a leading position for the production and marketing of citrus derivates.",
                "","During the day students will be able to experience the company which is a leader for the production and marketing of citrus derivates. They will be shown the production process of the fruit juices. It will be the unique opportunity to get an acquainted with the functioning of one of the major companies in this sector in Italy.");
        CompanyModel barcelona6 = new CompanyModel("Canditfrucht s.p.a", "VETPRO", "Via Medici, 373", "98051", "Barcellona P.G (Messina)", "Sicily, Italy",
                "(+39) 0909702531", "info@canditfrucht.com", "http://www.canditfrucht.com/", "Canditfrucht's industrial site extends on an area of more than 18.800 square metres of land and is in continuous expansion. Equipped with the most modern citrus fruits transformation and conservation machineries it strives to be at the forefront of production technology. These system allow today for the gross annual transformation of the highest fruit quality of over 51.492 kgs of fruits (orange, lemon, citrus, mandarine).",
                "", "A visit to the company which produces inter alia juices, candied fruit and essential oils. During the trip a tour of factory will be given, providing a close look at the production process.");
        CompanyModel barcelona7 = new CompanyModel("", "", "Via Colonna 17", "98059", "Rodi Milici (Messina)", "Sicily, Italy", "(+39) 090 9741052", "", "",
                "olive oil", "Fillipo da Campo", "The course will guide participants through the business hurdles of olive-oil production. They will learn the basic techniques of its production in one of the local companies. They will also learn about the importance of olive oil in the Italian culture, its nutrition and its impact on the human body.");
        CompanyModel barcelona8 = new CompanyModel("Salamita Soc. Coop.", "VETPRO", "Via Milite Ignoto, 29", "98051", "Barcellona P.G (Messina)", "Sicily, Italy",
                "(+39) 0909795930", "salmita@salamita.it", "http://www.salamita.it/", "The company was begun by in 1972 by the Salamita brothers.  The cooperation has played a significant contribution to the evolution of the biodynamic movement in Sicily by introducing the methods to treat plants exclusively with herbal medicines, quartz and manure to keep the food healthy, natural and rich of ingredients.",
                "Salamita brothers", "During the visit in the Salamita establishment located in Barcellona Pozzo di Gotto, which produces food but also is devoted to the production of fine wine, made with biodynamic vineyards planted on the biodynamic ground. Students will learn the most important details about the production of wine and other food products. ");
        CompanyModel bercelona9 = new CompanyModel("Bio Agriturismo Jalari", "VETPRO", "Contrada Maloto", "98051", "Barcellona P.G (Messina)", "Sicily, Italy", "(+39) 0909746245", "info@parcojalari.com", "www.parcojalari.com",
                "The food is prepared using traditional Italian recipes. The basis of taste are naturally growing food crops and vegetables, free of genetic modification and chemicals. Their production is consistent with the laws of nature. For the preparation of dishes using only the best quality ingredients.", "Antonino Pietrini Marketing Manager",
                "During a second visit to the Parco \"Jalari\" whose philosophy is \"bio-armonia\", students will be able to see the original production cycle and their nature. They will observe the cultivation techniques adopted which were a result of decades of experience in the field and constant collaboration with agronomists who specialize in organic sector. Students will learn the basic rules of traditional food production on Sicily. They will also attend a workshop about biological products consumption. It will be an excellent summary of the whole trip. ");
        CompanyModel barcelona10 = new CompanyModel("Gianfranco Buttò", "", "via Nazionale 214/B", "98050", "Terme Vigliatore (ME)", "Sicily, Italy", "(+39)3477591371", "", "",
                "Orticoltura", "Gianfranco Buttò", "");
        CompanyModel barcelona11 = new CompanyModel("Pollice Verde", "", "via del Mare 170/A", "98050", "Terme Vigliatore (ME)", "Sicily, Italy", "0039 3338174920", "tino.maio@cheapnet.it",
                "", "Orticoltura", "Tino Maio", "");
        CompanyModel barcelona12 = new CompanyModel("La Mora Siciliana s.p.a", "", " - Via degli Artigiani 6", "98051", "Barcellona Pozzo di Gotto", "Sicily, Italy", "'0039909791308", "info@lamorasiciliana.com", "http://www.lamorasiciliana.com/",
                "", "Maria Antonieta Calabrò", "");
        barcelonaCompanyList.add(barcelona1);
        barcelonaCompanyList.add(barcelona2);
        barcelonaCompanyList.add(barcelona3);
        barcelonaCompanyList.add(barcelona4);
        barcelonaCompanyList.add(barcelona5);
        barcelonaCompanyList.add(barcelona6);
        barcelonaCompanyList.add(barcelona7);
        barcelonaCompanyList.add(barcelona8);
        barcelonaCompanyList.add(bercelona9);
        barcelonaCompanyList.add(barcelona10);
        barcelonaCompanyList.add(barcelona11);
        barcelonaCompanyList.add(barcelona12);

        for (int i = 0; i<barcelonaCompanyList.size(); i++){
            barcelonaCompanyList.get(i).setKey("barcelona"+i);
        }
        for (int i = 0; i<cataniaCompanyList.size(); i++){
            cataniaCompanyList.get(i).setKey("catania"+i);
        }

        companiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CompanyModel companyModel = snapshot.getValue(CompanyModel.class);
                    companyModel.setKey(snapshot.getKey());
                    if (companyModel.getSelectedCountry()!=null){
                        if (companyModel.getSelectedCountry().equals("Barcelona P.G")){
                            barcelonaCompanyList.add(companyModel);
                        }else if (companyModel.getSelectedCountry().equals("Catania")){
                            cataniaCompanyList.add(companyModel);
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
