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

        lsmTable.put("1asd1", "13dqa1");
        lsmTable.put("1asd", "13dqa");
        lsmTable.put("1asd7", "13dqa7");
        lsmTable.put("1asd3", "13dqa3");
        lsmTable.put("1asd10", "13dqa10");
        lsmTable.put("1asd21", "13dqa21");
        lsmTable.put("1asd8", "13dqa8");

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

        lsmTable.put("asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("asd8", SSTableConstants.TOMB_STONE);

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

        lsmTable.put("q123w11asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("q123w11asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11231asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw12311asd3", SSTableConstants.TOMB_STONE);
        lsmTable.put("q123w11asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw12311asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11123asd8", SSTableConstants.TOMB_STONE);

        lsmTable.put("qw11345asd1", SSTableConstants.TOMB_STONE);
        lsmTable.put("q345w11asd", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw34511asd7", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11as345d3", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw11345asd10", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw43511asd21", SSTableConstants.TOMB_STONE);
        lsmTable.put("qw43511asd8", SSTableConstants.TOMB_STONE);
    }
}
