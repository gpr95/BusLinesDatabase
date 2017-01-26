package consts;

public enum WeekDays {
	 SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4),THURSDAY(5), FRIDAY(6), SATURDAY(7);

    private final int mask;

    private WeekDays(int mask)
    {
        this.mask = mask;
    }

    public int getMask()
    {
        return mask;
    }
}
