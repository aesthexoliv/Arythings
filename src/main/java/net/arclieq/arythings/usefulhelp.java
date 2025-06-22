// Just some help text, doesn't do anything in-game, just for reference.
/* Creating scoreboards help:
 * This is how to make the scoreboard method and call it.
 * Create EXACTLY this:
 * public static Scoreholder scoreHolder = new Scoreholder();
 * public static NumberFormat numberFormat = new NumberFormat() {@Override public MutableText format(int number) {return null;}@Override public NumberFormatType<? extends NumberFormat> getType() {return null;}}; * public static Scoreboard scoreboard = new Scoreboard();
 * public static ScoreboardObjective scoreboardObjective = scoreboard.addObjective("scoreboard_name_here", ScoreboardCriterion.DUMMY, Text.literal("scoreboard_name_here"), RenderType.INTEGER, false, numberFormat);
 * Then, in order to actually create it in-game, add another thing:
 * public static int callScoreboard(ScoreboardObjective objective) {
 * return objective;
 * }
 * Then, put callScoreboard(scoreboardObjective); in ServerLifecycleEvents.SERVER_STARTED.register((server) OR ArythingsMixin method loadWorld()v, callScoreboard(scoreboard) -> {
 * callScoreboard(scoreboard)
 * });
 */