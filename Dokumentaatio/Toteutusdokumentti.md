#Toteutus

###Rakenne

Tekoälyni logiikka koostuu kahdesta osasta: evaluaatiofunktiosta ja ailogic-luokasta. Evaluaatiofunktion vastuulla on pelitilanteiden arvostelu huomioiden molempien pelaajien nappuloiden materiaaliarvot, sijaintikohtaiset arvot ja liikkuvuus. Ailogic-luokka on vastuussa seuraavan siirron laskemisesta hyödyntäen tehostettua negamax-algoritmia. Negamax-algoritmissä kukin pelaaja pyrkii vuorollaan maksimoimaan huonoimman mahdollisen arvon, jonka vastustaja voi pakottaa pelaajan saamaan siirtojen seurauksena. 

Käytän negamaxin tehostamiseen useita eri keinoja, joten tässä niistä lyhyt esittely. Aloitetaanpa alfa-beeta karsinnalla, joka on kaikkien nopeutusten takana. Alfa-beeta karsinnan ideana on karsia varmasti parasta tähän astista siirtoketjua huonommat pois karsien parhaimmassa tapauksessa jopa 97% tapauksista syvyydellä 3 (ensimmäinen kokeiltu siirtoketju oli paras) ja huonoimmassa karsien 0 tapausta (siirtoketjut järjestyksessä huonoimmasta parhaimpaan). Näin alfa-beeta karsinta ei valitettavasti pienennä ohjelman O-notaatio aikavaativuutta, vaikka keskimääräinen aikavaativuus tippuukin todella paljon. Tämän vuoksi suurin osa muista nopeutuksista perustuukin siirtojen käsittelyyn paremmassa järjestyksessä. 

Alfa-beeta karsinnan lähtökohdat seuraavassa lyhyesti esimerkin avulla. Oletetaan, että meillä on pelipuussa solmu syvyydellä 1, josta paras löydetty siirtoketju johtaa paluu arvoon 5. Nyt tutkimme kyseisen solmun toista lasta c syvyydellä 2 ja huomaamme yhden lapsista olevan arvoltaan 4. Tästä seuraa, että syvyydellä 2 pelaavan pelaajan näkökulmasta lapsi on arvoltaan -4 ja koska tämä pelaaja maksimoi tilanteen arvon omasta näkökulmastaan, täytyy c:n olla arvoltaan vähintään -4. Näin syvyydellä 1 siirtonsa pelaavan pelaajan näkökulmasta c:n arvo voi olla enintään -(-4)=4<5 ja näin tiedämme, ettei muita c:n lapsia tarvitse tutkia, sillä ne eivät voi johtaa parannukseen. Näin alfa-beeta karsinta onkin sitä tehokkaampi, mitä nopeammin paras tähän astinen arvomme (kutsutaan alfa-arvoksi) lähestyy koko tutkinnan parasta arvoa.

Iteratiivisen syvennyksen ideana on, että halutessamme parhaan siirron rekursiosyvyydelle n asti yltävällä iteraatiolla, emme suoraan iteroi syvyydelle n. Sen sijaan iteroimme aluksi syvyydelle yksi kirjaten muistiin paras yksittäinen siirto, sitten iteroimme syvyydelle kaksi kokeillen ensin aloittaa parhaalla yksittäisellä siirrolla ja kirjaamme jälleen ylös paras kahden siirron ketju. Jatkaessamme tätä eteenpäin teemme pahimmassa tapauksessa paljon turhaa työtä (tutkimme 100/haarautuvuus prosenttia turhia solmuja), mutta shakissa tämä vastaa alle 2% turhaa työtä, kun taas säästämme alfa-beeta karsinnassa paljon enemmän. Ja mikä parasta, ylimääräinen työ on sitä pienempi, mitä enemmän mahdollisia siirtoja, eli mitä raskaampi operaatio olisi muulloin.

Lisäksi käytän heuristiikka, jonka perusteella valloitukset ovat todennäköisesti parempia siirtoja, kuin muut. Näin käsittelen valloitukset ennen muita siirtoja. Valloitukset voisi vielä jakaa voittaviin ja häviäviin, sillä voittavat valloitukset ovat lähes aina hyviä siirtoja (vastustajan kuningattaren lyöminen sotilaallani), kun taas loput voivat päättyä huonosti (lyön kuningattarella sotilaan, kuningattareni tulee vastustajan ratsun lyömäksi).

Viimeinen käyttämäni heuristiikka on 'tappajasiirrot'. Tappajasiirrot ovat siirtoja, jotka johtivat aiemmin samalla syvyydellä alfa-beta karsinnassa solmujen katkaisuun karsien osan pelipuusta pois. Säilytän näitä maksimissaan kolme jokaisella syvyydellä pelipuussa ja korvaan vanhimman aina uudella, jos uusi siirto löytyy. Näiden siirtojen tehostavan vaikutuksen takana on ns. 'killer-heuristic', jonka mukaan samalla rekursiosyvyydellä on luultavasti melko samanlainen tilanne, jolloin sama siirto on paras tai ainakin yksi parhaista kyseisessäkin tilanteessa.

Transpositiotaulun ideana on tallentaa dynaamiseen muistiin kyseisen pelitilanteen arvo molempien pelaajien näkökulmista, kun on kyseisen pelaajan vuoro ja iteraatio jatkuu vielä n siirtoa. Näin samassa tilanteessa ei arvoa tarvitse enää laskea uudelleen myöhemmin pelin aikana. Iteraation jatkuessa 0 askelta tulee siis kirjattua ylös kyseisen pelitilanteen saama arvo evaluaatiofunktiosta.

Toinen käyttämäni dynaamisen muistin sovellus on vanhojen iteraatioiden hyödyntäminen aloitettaessa seuraavan siirron etsiminen. Ennen iteraation aloitusta otan aiemman iteraation parhaasta siirtoketjusta p-t viimeistä siirtoa, missä p on plies eri maksimi rekursiosyvyys, jonon rekursiossa ollaan menossa, ja t on turns eli vuoroja viime iteraation suorituksesta. Nämä siirrot siirretään nyt järjestys ylläpitäen uuden parhaansirrtoketjun principalMoves kärkeen todennäköisinä vaihtoehtoina parhaan uuden ketjun aluksi. Saman lainen operaatio tehdään myös tappajasiirroille, jolloin meillä on heti alusta lähtien käytössä oletettavasti hyviä siirto vaihtoehtoja. Loput tappajasiirrot ja parhaan siirtoketjun osat alustetaan nulleiksi, jottei väärää tietoa käytettäisi.

Tekoälyn hyödyntämät tietorakenteet on koottu kansioon datastructures. Rakenteet vastaavat aika- ja tilavaativuuksiltaan Javan valmiita rakenteita ja olen toteuttanut vain työssäni tarvittavat metodit vähentääkseni työn kannalta turhaa urakkaa. Poikkeus on MYLimitedStack, joka on kiinteän kokoinen pino. Pinosta saadaan otettua päällimmäinen vakioajassa ja pinoon lisääminen onnistuu myös vakioajassa.

###Saavutetut aika- ja tilavaativuudet

Ohjelmani aikavaativuus on kaikkien tehostustenkin jälkeen O(218^d) eli tehostukset eivät vaikuta pahimman tapauksen aikavaativuuteen ollenkaan. Päin vastoin lisäämäni iteratiivinen syvennys lisää pahimman tapauksen aikavaativuutta hieman (noin 218^(d-1) lisää työtä eli 1/218 kokonaistyöstä). Sen sijaan ohjelmani keskimääräinen aikavaativuus lienee parantunut kivasti, sillä aluksi rekursiosyvyyden 3 tutkimiseen vaadittiin jopa 20 sekuntia, kun taas lopuksi siitä selvitään alle sekunnissa lähes aina. Tämä O-notaatiohan ei todellakaan vastaa pelini todellista aikavaativuutta, sillä O-notaatiota laskiessa on oletettu kaikkien käyttämieni heuristiikkojen olevan aina väärässä, vaikka tämä on hyvin epätodennäköistä. Ohjelmani muistuttaakin tässä QuickSorttia, jolla havaitaan samanlainen ilmiö. Valitettavasti en osaa laskea ohjelmani keskimääräistä aikavaativuutta sanoakseni tarkemman arvion.

Ohjelmani on rakennettu periaatteella, jossa aikavaativuus on paljon tilavaativuutta olennaisempi eli käytän niin paljon dynaamista muistia kuin mahdollista. Tämän vuoksi ohjelmani tilavaativuuskin on karu O(218^d), sillä joudumme pahimmassa tapauksessa tallentamaan jokaisen tutkimamme pelitilanteen muistiin. Tätä voisi laskea esimerkiksi ottamalla vain tietyn määrän yleisimpiä pelitilanteita muistiin, mutta en tämän kurssin aikana ole ehtinyt suunnitella tähän mitään monimutkaisempaa vaan keskittynyt algoritmin nopeuttamiseen. Mikäli tilanteista haluaisi tallentaa vain osan, pitäisi minun luoda jonkinlainen arvostelusysteemi pelitilanteille. Tämän arvostelujärjestelmän tulisi huomioida ainakin rekursion jäljellä oleva syvyys (mitä korkeammalla puussa olevia haaroja karsitaa, sitä parempi) ja tilanteiden esiintymistiheys.

###Puutteet ja parannusehdotukset

Shakissani peli ei tosiaan lopu, jos kuluu 50 vuoroa ilman nappuloiden syömistä, kuten virallisissa säännöissä sanotaan. Lisäksi pelini ei tunnista kaikkia pelitilanteita, joissa pelaajien materiaali ei enää riitä matitukseen, vaan peli jää jumiin, kunne sama tilanne toistuu kolmannen kerran.

Matitus on ylipäätään hankalaa tekoälylle, jos se vaatisi sotilaiden korotuksen. Sotilaiden korotus vaatisi usein monta siirtoa, eikä rekursiosyvyys riitä millään näiden kaikkien tarkasteluun. Lisäksi sotilaiden arvo kasvaa merkittävästi vasta niiden ollessa hyvin lähellä vastustajan päätyä, eikä pelaajan siis usein kannata juuri siirtää niitä. Tähän vaikuttaa myös se, että pelaaja menettää liikkuvuutta siirtäessään sotilasta ensimmäisen kerran ja sotilaiden arvo aloitus ruudussa on jo muutenkin varsin korkea niiden kuninkaan suojausarvon vuoksi. Näin sotilaat voivat usein jämähtää loppupelissä paikalleen ja pelipäättyä tasapeliin, vaikka se oli varma voitto. Tätä varten voisin lisätä peliini ns. [Contempt-factorin](https://chessprogramming.wikispaces.com/Contempt+Factor) eli tekoälyn hallitessa peliä, alkaa se hyväksyä pieniä tappioita matittaakseen vastustajan. Näin tekoäly välttää tasapelin aiheuttamisen voitetussa pellissä. Toisaalta tekoäly myös hyödyntää kuningasta harvoin matituksessa, joten voisin antaa kuninkaan sijainnille eri arvot loppupelissä, kuten lähteessäni oli tehtykin. Tämä tietenkin vaatisi sen määrittämisen, milloin loppupeli alka ja on kannattavampaa siirtää kuningas keskemmäs lautaa.

Edellä mainittujen lisäksi, voisin lisätä työhöni ns. [SEE(static exchange evaluation)-algoritmin](https://chessprogramming.wikispaces.com/Static+Exchange+Evaluation), jolla laskisin mahdollisten vaihtokauppojen arvot lyötäessä vastustajan nappula tietystä ruudusta. Näin tekoäly voisi tarvittaessa nähdä useamman ruudun eteenpäin kuin yleensä juuri lisäämättä aikavaativuutta. Samaa valinnaisuutta voisi soveltaa myös esimerkiksi vain riittävän hyviin siirtoketjuihin vähentäen työtä. Voisin myös kokeilla Best-First lähetymistapaa, jossa oletetaan siirron olevan paras ja varmistetaan tämä hyvin nopeasti, mutta jos ei ole, käytetään alfa-beetaa. Esimerkkejä tästä ovat [MTD-f](https://en.wikipedia.org/wiki/MTD-f) ja [NegaScout](https://en.wikipedia.org/wiki/Principal_variation_search).

Lisäksi käyttämäni lineaarinen evaluaatiofunktio on hyvin minimalistinen, joten voisin lisätä siihen erilaisia osatekijöitä, kuten [kuninkaan turvallisuus](https://chessprogramming.wikispaces.com/King+Safety) ja [sotilasrakenne](https://chessprogramming.wikispaces.com/Pawn+Structure). Näistä kuninkaan turvallisuuteen en valitettavasti löytänyt helppoa funktiota ja sotilasrakenne olisi vaatinut tarvittavan hashaustaulun luonnin ja rakenteiden evaluoinnin, jota en itse shakki taidottomana olisi osannut.

###Lähteet

[Pelitilanteen arviointi](https://chessprogramming.wikispaces.com/Evaluation)

[Suurin määrä mahdollisia siirtoja pelaajalla 218](https://www.chess.com/forum/view/fun-with-chess/what-chess-position-has-the-most-number-of-possible-moves)

[Nappuloiden arvot](https://en.wikipedia.org/wiki/Chess_piece_relative_value)

[Haun tehostusmenetelmät](https://chessprogramming.wikispaces.com/Search)

[Alpha-beta karsinta](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning)

[Evaluaatiofunktio ja minmax](https://chessprogramming.wikispaces.com/Evaluation)

[Mahdolliset siirrot](https://www.chess.com/chessopedia/view/mathematics-and-chess)

[Suurin määrä mahdollisia siirtoja](https://www.chess.com/forum/view/fun-with-chess/what-chess-position-has-the-most-number-of-possible-moves)

[Materiaaliarvo](https://en.wikipedia.org/wiki/Chess_piece_relative_value)

[Iterative deepening](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search)

[NegaMax-algoritmi](https://en.wikipedia.org/wiki/Negamax)

[Sijaintikohtaiset arvot](https://chessprogramming.wikispaces.com/Simplified+evaluation+function)

[Principal variation](https://chessprogramming.wikispaces.com/Principal+variation?responseToken=9eebfa4cd8351cf79afdf2a772da9d99)

[Transpositiotaulu](https://en.wikipedia.org/wiki/Transposition_table)
