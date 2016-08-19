#Aihemäärittely

Työn aiheena on luoda tekoäly JavaLabra-kurssilla luotuun [shakkiin](https://github.com/salsam/Samin-shakki). Tekoälyn logiikka toteutetaan pakkaukseen chess.logic.ailogic ja tietorakenteet kansioon chess.domain.datastructures. Tekoälyn toteutuksessa käytetään evaluaatiofunktiota, jolla annetaan jokaiselle pelitilanteelle arvo kyseisen pelaajan kannalta. Tätä evaluaatiofunktiota sovelletaan sitten, pelissä 3 vuoroa eteenpäin niin, että oletetaan vastustajan valitsevan aina ne siirrot, joilla saadaan mahdollisimman huono tilanteen arvo. Näitä saatuja arvoja vastaavista siirroista suoritetaan se, jolle saadaan tällä minmax-algoritmilla kaikkein suurin arvo. Lisäksi, jottei tekoälyjen välinen peli etenisi aina täsmälleen samalla tavalla, jos useat eri siirrot saavat saman arvon, arvotaan niistä yksi ja toteutetaan se.

Minmax-algoritmin lyhentämiseksi korvasin sen negamax-algoritmilla, joka toimii aivan kuten minmax - paitsi, että min-haara on korvattu maxilla käyttäen kaavaa min(a,b)=-max(-a,-b). Nopeutan algoritmia monilla tavoin, joiden toimintaa olen kommentoinut myös javadocissa. Käytän negamaxin nopeutukseen alfa-beeta karsintaa, joka perustuu turhien haarojen katkaisuun, kun olemme täysin varmoja ettei niistä löydy parempaa vaihtoehtoa. Oletetaan, että paras tähän mennessä löytämämme arvo pelipuun maksimointisolmulle a on 10 ja että olemme tutkimassa sen lasta b, jossa minimoidaan arvo (vastustajan vuoro). Nyt löydämme b:n lapsen, jolla on arvo 5, joten tiedämme, että koska b:n arvo on minimi sen lasten arvoista, voi b:n arvo olla enintään 5. Näin b:n arvo ei voi muuttaa enää parasta a:lle löydettyä arvoa, eikä siis b:n muita lapsia tarvitse tutkia.

Tehostaakseni alfa-beeta karsintaa pyrin käymään siirrot mahdollisimman hyvässä järjestysessa, sillä löydettyämme parhaan siirron, ei meidän enää tarvitse tutkia lopuista solmuista kuin yksi lapsi. Hyödyntääkseni tätä järjestämisen ideaa, käytän niin kutsuttua iteratiivista syventämistä, jossa tutkitaan ensin 1 vuoro eteenpäin, sitten aloitetaan alusta tutkien syvyydelle kaksi jne. Näin voimme kirjoittaa ylös parhaan tähän mennessä löydetyn siirto-kombinaation ja tutkia tämän ensimmäisenä. Tämä on todennäköisesti yksi parhaimmista vaihtoehdoista, joten saamme karsittua pelipuusta suuren osan pois (kunhan heuristiikkamme toimii). Käytän myös hyödykseni transpositiotauluja, joihin tallennetaan tilanteen arvo, jos se on laskettu jo aiemmin samalla rekursiosyvyydellä. Näin dynaamisen muistin avulla vältämme tilanteiden arvioinnin moneen kertaan.

Tarkoituksenani on lisätä vielä työhöni ns. tappaja-heuristiikka ja käydä ensin läpi siirrot, joilla saadaan vastustajan nappula lyödyksi.

Aluksi käytetään evaluaatiofunktiona vain klassista 1-3-3-5-9 arvostelua shakkinappuloille, mutta tarkoituksenani on monimutkaistaa evaluaatiofunktiota huomioiden esimerkiksi nappulan sijainnin laudalla (keskikohta arvokkaampi) ja pelin vaihe. Huomioin myös nappuloiden siirtomahdollisuudet, sillä useampi siirtomahdolisuus mahdollistaa paremman reagoinnin vastustajan siirtoihin. Luonnollisesti evaluaatiofunktio laskee matituksen arvoksi äärettömän (200000), sillä se lopettaa pelin.

####Tietorakenteet

Tietorakenteet toteutetaan pakkaukseen domain.datastructures. Käytän työssäni tietorakenteein HashMappia, HashSettiä ja ArrayListiä, jotka korvataan myöhemmin omilla versioilla. Tietorakenteet on valittu niitten tehokkuuden takia. HashSetistä saa vakioajassa selville sisältääkö se esimerkiksi ruudun (3,3). HashMappia taas käytetään luokkakohtaisten sijaintiarvojen saamisen vakioajassa, jolloin jokaiselle luokalle voidaan antaa ruutukohtainen arvo esimerkiksi tornit ovat arvokkaampia päädyissä kuin keskellä ja lähetillä tämä on täysin toisin päin. ArrayListiin taas on helppo tallentaa parhaita siirtoja ja ottaa sitten niistä satunnainen vakioajassa random accessin avulla.

####Aikavaativuus

Työn aikavaativuudelle on vaikea sanoa mitään tiettyä lukua, sillä suunnittelin tekeväni tekoälylle eri vaikeustasoja, jotka eroaisivat toisistaan maximin-algoritmin syvyydessä. Tämä kuitenkin niin, että kaikki lisäämäni vaikeustasot tekisivät siirron kohtuullisessa ajassa (vaikein max 10s). Oletus vaikeustasolla haluan siirtojen tapahtuvan alle sekunnissa, joka tekisi shakista hyvin käyttäjäystävällisen. Kokonaisaikavaativuus olisi O(Σ\_(a ∈ l)Σ\_(b ∈ l\_a)Σ\_(c ∈ l\_ab) n), missä l on mahdollisten siirtojen joukko aluksi, l\_a on mahdollisten siirtojen joukko, kun siirto a on suoritettu, ja l\_ab mahdollisten siirtojen joukko, kun siirrot a ja b suoritettu. Tämä on valitettavasti todella sekava kaava, sillä sain harmikseni selville, ettei github tue matemaattisialausekkeita millään tavalla, enkä keksinyt mitään parempaa tapaa ilmaista asiaa Unicodella. 

Lisäksi tekoälyä sunnitellessa on tehtävä kompromissejä evaluaatiofunktion monimutkaisuuden ja maximinin rekursiosyvyyden suhteen. Näin maksimoidessani tekoälyn taitoa, kokeilen muutamaa eri ominaisuutta evaluaatiofunktiossa. Ruutukohtainen  nappuloiden materiarvo on kuitenkin ehdottomoasti mukana, sillä sen lisääminen aiheuttaa vain vakioaikaisen muutoksen aikavaativuuteen. Tämän lisäksi lisään myös liikkuvuuden eli pelaajan mahdollisten siirtojen lukumäärän arvosteluun, sillä se mahdollistaa paremman reagoinnin vastustajan siirtoihin. Tämä muutos kuitenkin vaatii kaikkien nappuloiden siirtojen laskemista, jonka aikavaativuus on O(n+m), missä n on laudalla jäljellä olevien pelaajan nappuloiden määrä ja m vastaavista vastustajan nappuloiden määrä. Lisäksi n+m <=32, joten evaluaatiofunktio on vakioaikainen. Periaatteessa haluan kuitenkin syvyyden olevan ainakin 3 plytä, jotta tietokone pystyisi voittamaan minun tapaiseni aloittelijan helposti. Näin en lisää muita monimutkaistuksia evaluaatifuntioon. Tällä tavalla saadaan evaluaatiofunktion aikavaativuudeksi saadaan O(n), sillä jokaiselta pelaajan nappulalta lasketaan sen tilannekohtainen materiaaliarvo ja liikkuvuusarvo ja summataan nämä yhteen vähentäen vastustustajan vastaava summa.

Näin maximin-algoritmin aikavaativuus on O((m^d\*n)), missä m on pelaajan liikkuvuus eli mahdollisten siirtojen lukumäärä, n on laudalla jäljellä olevien nappuloiden määrä ja d rekursiosyvyys. Tässä tietenkin oletus, että molemmilla pelaajilla olisi sama liikkuvuus jokaisen siirron jälkeen. Parempi arvio saadaankin, jos etsitään yläraja pelaajan liikkuvuudelle. Oletetaan, että pelaajalla on tavalliset aloitusnappulat eli 8 sotilasta, 2 ratsua, 2 lähettiä, 2 tornia, kuningas ja kuningatar. Nyt nappuloiden yhteenlaskettu potentiaalinen liikkuvuus on maksimissaan 160 (lasketaan nappuloiden nykyinen paikkamukaan linjoja pitkin liikkuvilla nappuloilla), tämän ylitys tai saavuttaminen on käytännössä mahdotonta. Näin saamme lopulliseksi ylärajaksi aikavaativuudelle O(160^d\*n), jossa tietenkin voi arvioida vielä n<=32 eli aikavaativuus on maksimissaan O(32\*160^d). 

Todellisuudessa aikavaativuus tulee kuitenkin olemaan huomattavasti pienempi, sillä [käytännössä yli 60 mahdollisuutta on hyvin harvinaista](https://www.chess.com/chessopedia/view/mathematics-and-chess). Näin voisimme käyttää aikavaativuuden arviossa arvoa ns. käytännöllisenä arviona 32\*60^d saadaksemme tarkemman kuvan tekoälyn todellisuudessa vaatimasta ajasta. Lisäksi mikäli yhdellä pelaajalla on todella paljon vaihtoehtoja, toisen pelaajan vaihtoehdot ovat vastavuoroisesti suppeammat ja tämä tasoittaa tilannetta syvyyden ollessa yli 1.

####Lähteet:
[Evaluation function and maximin](https://chessprogramming.wikispaces.com/Evaluation)

[Amount of possible moves](https://www.chess.com/chessopedia/view/mathematics-and-chess)

[Different material values for chess pieces](https://en.wikipedia.org/wiki/Chess_piece_relative_value)

[Iterative deepening](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search)

[NegaMax-algorithm](https://en.wikipedia.org/wiki/Negamax)

[Positional values for pieces of each class] (https://chessprogramming.wikispaces.com/Simplified+evaluation+function)

[Principal variation](https://chessprogramming.wikispaces.com/Principal+variation?responseToken=9eebfa4cd8351cf79afdf2a772da9d99)

[Transposition tables](https://en.wikipedia.org/wiki/Transposition_table)
