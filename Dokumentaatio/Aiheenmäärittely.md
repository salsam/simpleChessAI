#Aihemäärittely

Työn aiheena on luoda tekoäly JavaLabra-kurssillä luotuun [shakkiin](https://github.com/salsam/Samin-shakki). Tekoälyn toteutuksessa käytetään evaluaatiofunktiota, jolla annetaan jokaiselle pelitilanteelle arvo kyseisen tekoälyn kannalta. Tätä evaluaatiofunktiota sovelletaan sitten, pelissä 3 vuoroa eteenpäin niin, että oletetaan vastustajan valitsevan aina ne siirrot, joilla saadaan mahdollisimman huono tilanteen arvo. Näitä saatuja arvoja vastaavista siirroista suoritetaan se, jolle saadaan tällä maxi-min-algoritmilla kaikkein suurin arvo. Lisäksi, jottei tekoälyjen välinen peli etenisi aina täsmälleen samalla tavalla, jos useat eri siirrot saavat saman arvon, arvotaan niistä yksi ja toteutetaan se.

Aluksi käytetään evaluaatiofunktiona vain klassista 1-3-3-5-9 arvostelua shakkinappuloille, mutta tarkoituksenani on monimutkaistaa evaluaatiofunktiota huomioiden esimerkiksi nappulan sijainnin laudalla (keskikohta arvokkaampi) ja pelin vaihe. Huomoioin myös nappuloiden siirtomahdollisuudet, sillä useampi siirtomahdolisuus mahdollistaa paremman reagoinnin vastustajan siirtoihin. Luonnollisesti evaluaatiofunktio laskee matituksen arvoksi äärettömän (200000), sillä se lopettaa pelin.

####Tietorakenteet

Käytän työssäni tietorakenteein HashMappia, HashSettiä ja ArrayListiä, jotka korvataan myöhemmin omilla versioilla. Tietorakenteet on valittu niitten tehokkuuden takia. HashSetistä saa vakioajassa selville sisältääkö se esimerkiksi ruudun (3,3). HashMappia taas käytetään luokkakohtaisten sijaintiarvojen saamisen vakioajassa, jolloin jokaiselle luokalle voidaan antaa ruutukohtainen arvo esimerkiksi tornit ovat arvokkaampia päädyissä kuin keskellä ja lähetillä tämä on täysin toisin päin. ArrayListiin taas on helppo tallentaa parhaita siirtoja ja ottaa sitten niistä satunnainen vakioajassa random accessin avulla. ArrayListin voisi oikeastaan korvata hyvin esimerkiksi linked listillä, sillä parhaita siirtoja ei pitäisi tulla paljon, jos teen evaluaatiofunktiosta monimutkaisen.

####Aikavaativuus

Työn aikavaativuudelle on vaikea sanoa mitään tiettyä lukua, sillä suunnittelin tekeväni tekoälylle eri vaikeustasoja, jotka eroaisivat toisistaan maximin-algoritmin syvyydessä. Tämä kuitenkin niin, että kaikki lisäämäni vaikeustasot tekisivät siirron kohtuullisessa ajassa (vaikein max 10s). Oletus vaikeustasolla haluan siirtojen tapahtuvan alle sekunnissa, joka tekisi shakista hyvin käyttäjäystävällisen.

Lisäksi tekoälyä sunnitellessa on tehtävä kompromissejä evaluaatiofunktion monimutkaisuuden ja maximinin rekursiosyvyyden suhteen. Näin maksimoidessani tekoälyn taitoa, kokeilen muutamaa eri ominaisuutta evaluaatiofunktiossa. Ruutukohtainen  nappuloiden materiarvo on kuitenkin ehdottomoasti mukana, sillä sen lisääminen aiheuttaa vain vakioaikaisen muutoksen aikavaativuuteen. Tämän lisäksi lisään myös liikkuvuuden eli pelaajan mahdollisten siirtojen lukumäärän arvosteluun, sillä se mahdollistaa paremman reagoinnin vastustajan siirtoihin. Tämä muutos kuitenkin vaatii kaikkien nappuloiden siirtojen laskemista, jonka aikavaativuus on O(n+m), missä n on laudalla jäljellä olevien pelaajan nappuloiden määrä ja m vastaavista vastustajan nappuloiden määrä. Lisäksi n+m <=32, joten evaluaatiofunktio on vakioaikainen. Periaatteessa haluan kuitenkin syvyyden olevan ainakin 3 plytä, jotta tietokone pystyisi voittamaan minun tapaiseni aloittelijan helposti. Näin en lisää muita monimutkaistuksia evaluaatifuntioon. Tällä tavalla saadaan evaluaatiofunktion aikavaativuudeksi saadaan O(n), sillä jokaiselta pelaajan nappulalta lasketaan sen tilannekohtainen materiaaliarvo ja liikkuvuusarvo ja summataan nämä yhteen vähentäen vastustustajan vastaava summa.

Näin maximin-algoritmin aikavaativuus on O((m^d\*n)), missä m on pelaajan liikkuvuus eli mahdollisten siirtojen lukumäärä, n on laudalla jäljellä olevien nappuloiden määrä ja d rekursiosyvyys. Tässä tietenkin oletus, että molemmilla pelaajilla olisi sama liikkuvuus jokaisen siirron jälkeen. Parempi arvio saadaankin, jos etsitään yläraja pelaajan liikkuvuudelle. Oletetaan, että pelaajalla on tavalliset aloitusnappulat eli 8 sotilasta, 2 ratsua, 2 lähettiä, 2 tornia, kuningas ja kuningatar. Nyt nappuloiden yhteenlaskettu potentiaalinen liikkuvuus on maksimissaan 160 (lasketaan nappuloiden nykyinen paikkamukaan linjoja pitkin liikkuvilla nappuloilla), tämän ylitys tai saavuttaminen on käytännössä mahdotonta. Näin saamme lopulliseksi ylärajaksi aikavaativuudelle O(160^d\*n), jossa tietenkin voi arvioida vielä n<=32 eli aikavaativuus on maksimissaan O(32\*160^d). 

Todellisuudessa aikavaativuus tulee kuitenkin olemaan huomattavasti pienempi, sillä [käytännössä yli 60 mahdollisuutta on hyvin harvinaista](https://www.chess.com/chessopedia/view/mathematics-and-chess). Näin voisimme käyttää aikavaativuuden arviossa arvoa ns. käytännöllisenä arviona 32\*60^d saadaksemme tarkemman kuvan tekoälyn todellisuudessa vaatimasta ajasta. Lisäksi mikäli yhdellä pelaajalla on todella paljon vaihtoehtoja, toisen pelaajan vaihtoehdot ovat vastavuoroisesti suppeammat ja tämä tasoittaa tilannetta syvyyden ollessa yli 1.

####Lähteet:
[Evaluation function and maximin](https://chessprogramming.wikispaces.com/Evaluation)

[Amount of possible moves](https://www.chess.com/chessopedia/view/mathematics-and-chess)

[Different material values for chess pieces](https://en.wikipedia.org/wiki/Chess_piece_relative_value)

[Positional values for pieces of each class] (https://chessprogramming.wikispaces.com/Simplified+evaluation+function)
