public class testDriver {
    public static void main(String[] args)
    {
        LSMTable lsmTable = new LSMTable();
        lsmTable.put("asd1", "3dqa1");
        lsmTable.put("asd", "3dqa");
        lsmTable.put("asd7", "3dqa7");
        lsmTable.put("asd3", "3dqa3");
        lsmTable.put("asd10", "3dqa10");
        lsmTable.put("asd21", "3dqa21");
        lsmTable.put("asd8", "3dqa8");

        lsmTable.put("1asd1", "13dqa1");
        lsmTable.put("1asd", "13dqa");
        lsmTable.put("1asd7", "13dqa7");
        lsmTable.put("1asd3", "13dqa3");
        lsmTable.put("1asd10", "13dqa10");
        lsmTable.put("1asd21", "13dqa21");
        lsmTable.put("1asd8", "13dqa8");

        lsmTable.put("2asd1", "23dqa1");
        lsmTable.put("2asd", "23dqa");
        lsmTable.put("2asd7", "23dqa7");
        lsmTable.put("2asd3", "23dqa3");
        lsmTable.put("2asd10", "23dqa10");
        lsmTable.put("2asd21", "23dqa21");
        lsmTable.put("2asd8", "23dqa8");

        System.out.println(lsmTable.get("asd1") + " verify is: 3dqa1");

        lsmTable.put("11asd1", "113dqa1");
        lsmTable.put("11asd", "113dqa");
        lsmTable.put("11asd7", "113dqa7");
        lsmTable.put("11asd3", "113dqa3");
        lsmTable.put("11asd10", "113dqa10");
        lsmTable.put("11asd21", "113dqa21");
        lsmTable.put("11asd8", "113dqa8");

        lsmTable.put("111asd1", "1113dqa1");
        lsmTable.put("111asd", "1113dqa");
        lsmTable.put("111asd7", "1113dqa7");
        lsmTable.put("111asd3", "1113dqa3");
        lsmTable.put("111asd10", "1113dqa10");
        lsmTable.put("111asd21", "1113dqa21");
        lsmTable.put("111asd8", "1113dqa8");

        lsmTable.put("112asd1", "1123dqa1");
        lsmTable.put("112asd", "1123dqa");
        lsmTable.put("112asd7", "1123dqa7");
        lsmTable.put("112asd3", "1123dqa3");
        lsmTable.put("112asd10", "1123dqa10");
        lsmTable.put("112asd21", "1123dqa21");
        lsmTable.put("112asd8", "1123dqa8");

        lsmTable.put("qw2asd1", "qw23dqa1");
        lsmTable.put("qw2asd", "qw23dqa");
        lsmTable.put("qw2asd7", "qw23dqa7");
        lsmTable.put("qw2asd3", "qw23dqa3");
        lsmTable.put("qwasd10", "qw23dqa10");
        lsmTable.put("qw2asd21", "qw23dqa21");
        lsmTable.put("qw2asd8", "qw23dqa8");

        lsmTable.put("qw11asd1", "qw113dqa1");
        lsmTable.put("qw11asd", "qw113dqa");
        lsmTable.put("qw11asd7", "qw113dqa7");
        lsmTable.put("qw11asd3", "qw113dqa3");
        lsmTable.put("qw11asd10", "qw113dqa10");
        lsmTable.put("qw11asd21", "qw113dqa21");
        lsmTable.put("qw11asd8", "qw113dqa8");

        lsmTable.put("1asd1", "13dqa2341");
        lsmTable.put("1asd", "13dqa234");
        lsmTable.put("1asd7", "13dqa7234");
        lsmTable.put("1asd3", "13dqa3234");
        lsmTable.put("1asd10", "13dqa10234");
        lsmTable.put("1asd21", "13dqa21234");
        lsmTable.put("1asd8", "13dqa8234");

        System.out.println(lsmTable.get("asd1") + " verify is: 3dqa1");
        System.out.println(lsmTable.get("112asd8") + " verify is: 1123dqa8");

        lsmTable.put("2asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("2asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("11asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("11asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("asd1", "1");
        lsmTable.put("asd", "2");
        lsmTable.put("asd7", "3");
        lsmTable.put("asd3", "4");
        lsmTable.put("asd10", "5");
        lsmTable.put("asd21", "6");
        lsmTable.put("asd8", "7");

        lsmTable.put("1asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("1asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("qw2asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw2asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw2asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw2asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("qwasd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw2asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw2asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("qw11asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("q123w11asd1", "sfadasfasatertetett4");
        lsmTable.put("q123w11asd", "sfadasfasayhrjyt4");
        lsmTable.put("qw11231asd7", "sfadasfasa456wt4");
        lsmTable.put("qw12311asd3", "sfadasfasat4jr7j");
        lsmTable.put("q123w11asd10", "sfadasfasatheret4");
        lsmTable.put("qw12311asd21", "sfadasfavwyewj7sat4");
        lsmTable.put("qw11123asd8", "sfadasfasa4w3tewt4");

        lsmTable.put("qw11345asd1", "sfada2c4td3sfasat4");
        lsmTable.put("q345w11asd", "sfadasfasavc345t4");
        lsmTable.put("qw34511asd7", "sfadasfas6ctvy6yat4");
        lsmTable.put("qw11as345d3", "sfadasfascq34tcdat4");
        lsmTable.put("qw11345asd10", "sfadasfasq23rs23at4");
        lsmTable.put("qw43511asd21", "sfadasfas24rcat4");
        lsmTable.put("qw43511asd8", "sfadasfasctqw4cat4");

        lsmTable.put("q123w32111asd1", "sfadasfasatertetett4");
        lsmTable.put("q123w32111asd", "sfadasfasayhrjyt4");
        lsmTable.put("qw11231231asd7", "sfadasfasa456wt4");
        lsmTable.put("qw12332111asd3", "sfadasfasat4jr7j");
        lsmTable.put("q123w13211asd10", "sfadasfasatheret4");
        lsmTable.put("qw12311231asd21", "sfadasfavwyewj7sat4");
        lsmTable.put("qw11123321asd8", "sfadasfasa4w3tewt4");

        lsmTable.put("qw113451231asd1", "sfada2c4td3sfas34534at4");
        lsmTable.put("q345w111231asd", "sfadasfasavc33453445t4");
        lsmTable.put("qw34511asd1237", "sfadasfas6ct345vy6yat4");
        lsmTable.put("qw11as3412315d3", "sfadasfascq34tcdat43453");
        lsmTable.put("qw11345asd23110", "sfadasfasq23rs2435343at4");
        lsmTable.put("qw43511asd233111", "sfadasfas3453424rcat4");
        lsmTable.put("qw43511asd321238", "sfada4353sfasctqw4cat4");

        System.out.println(lsmTable.get("asd"));
        System.out.println(lsmTable.get("q123w11asd1"));
        System.out.println(lsmTable.get("qw11asd8"));

    }
}
