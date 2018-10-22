package net.sf.jremoterun.utilities.nonjdk.sfdownloader

/**
 * https://sourceforge.net/p/forge/documentation/Mirrors/
 * FIXME: issue with cert for all.
 */
enum Servers {


    astuteinternet('Vancouver, BC'),
    ayera('Modesto, CA'),
    cfhcable('United States'),
    cytranet('Fremont, CA'),
    datapacket('Prague, Czech Republic'),
    freefr('Paris, France'),
    iweb('Montreal, QC'),
    jaist('Nomi, Japan'),
    kent('Canterbury, United Kingdom'),
    liquidtelecom('Kenya'),
    netcologne('Cologne, Germany'),
    netix('Bulgaria'),
    newcontinuum('West Chicago, IL'),
    phoenixnap('Tempe, AZ'),
    razaoinfo('Passo Fundo, Brazil'),
    svwh('San Jose, CA'),
    tenet('Wynberg, South Africa'),
    ufpr('Curitiba, Brazil'),
    versaweb('Las Vegas, NV'),
    vorboss('London, United Kingdom'),



    ;

    String description

    Servers(String description) {
        this.description = description
    }


    public static List<Servers> all = values().toList()
}