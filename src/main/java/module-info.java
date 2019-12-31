module com.code.fauch.horcrux.h2 {
    requires com.code.fauch.horcrux;
    requires com.h2database;
    provides com.code.fauch.horcrux.spi.IHorcrux with com.code.fauch.horcrux.h2.H2Horcrux;
}