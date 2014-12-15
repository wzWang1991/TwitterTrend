import java.util.List;


public class TrendData {
	public Time Time;
	public int PositiveSum;
	public int NegativeSum;
	public List<WordCount> WordCounts;
	
	public class Time {
		int Day;
		int Hour;
	}
	
	public class WordCount {
		String Word;
		int Count;
	}
}
