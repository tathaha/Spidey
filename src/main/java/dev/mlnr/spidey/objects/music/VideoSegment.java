package dev.mlnr.spidey.objects.music;

public class VideoSegment
{
    private final long segmentStart;
    private final long segmentEnd;

    public VideoSegment(final long segmentStart, final long segmentEnd)
    {
        this.segmentStart = segmentStart;
        this.segmentEnd = segmentEnd;
    }

    public long getSegmentStart()
    {
        return this.segmentStart;
    }

    public long getSegmentEnd()
    {
        return this.segmentEnd;
    }
}