package com.chua.common.support.lang.process;



import com.chua.common.support.utils.StringUtils;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Function;

import static com.chua.common.support.lang.process.StringDisplayUtils.getStringDisplayLength;
import static com.chua.common.support.lang.process.StringDisplayUtils.trimDisplayLength;


/**
 * Default progress bar renderer (see {@link ProgressBarRenderer}).
 *
 * @author Tongfei Chen
 * @author Muhammet Sakarya
 * @since 0.8.0
 */
public class DefaultProgressBarRenderer implements ProgressBarRenderer {

    private ProgressBarStyle style;
    private String unitName;
    private long unitSize;
    private boolean isSpeedShown;
    private DecimalFormat speedFormat;
    private ChronoUnit speedUnit;
    private boolean isEtaShown;
    private Function<ProgressState, Optional<Duration>> eta;

    protected DefaultProgressBarRenderer(
            ProgressBarStyle style,
            String unitName,
            long unitSize,
            boolean isSpeedShown,
            DecimalFormat speedFormat,
            ChronoUnit speedUnit,
            boolean isEtaShown,
            Function<ProgressState, Optional<Duration>> eta
    ) {
        this.style = style;
        this.unitName = unitName;
        this.unitSize = unitSize;
        this.isSpeedShown = isSpeedShown;
        this.speedFormat = isSpeedShown && speedFormat == null ? new DecimalFormat() : speedFormat;
        this.speedUnit = speedUnit;
        this.isEtaShown = isEtaShown;
        this.eta = eta;
    }

    // Number of full blocks
    protected int progressIntegralPart(ProgressState progress, int length) {
        return (int) (progress.getNormalizedProgress() * length);
    }

    protected int progressFractionalPart(ProgressState progress, int length) {
        double p = progress.getNormalizedProgress() * length;
        double fraction = (p - Math.floor(p)) * style.fractionSymbols.length();
        return (int) Math.floor(fraction);
    }

    protected String etaString(ProgressState progress) {
        Optional<Duration> eta = this.eta.apply(progress);
        if (eta.isPresent()) {
            return Util.formatDuration(eta.get());
        } else {
            return "?";
        }
    }

    protected String percentage(ProgressState progress) {
        String res;
        if (progress.max <= 0 || progress.indefinite) {
            res = " ";
        } else {
            res = String.valueOf((int) Math.floor(100.0 * progress.current / progress.max)) + "%";
        }

        if (progress.ansi) {
            return "\u001b[38;5;196m" + Util.repeat(' ', 4 - res.length()) + res + "\u001b[0;m ";
        }
        return Util.repeat(' ', 4 - res.length()) + res;
    }

    protected String ratio(ProgressState progress) {
        ProgressStyle progressStyle = progress.getProgressStyle();
        String m = progressStyle.format(progress.indefinite ? "?" : String.valueOf(progress.max / unitSize));
        if("?".equals(m)) {
            m = "";
        }
        if(progressStyle != ProgressStyle.LOADING) {
            String c = progressStyle.format(String.valueOf(progress.current / unitSize));
            return Util.repeat(' ', m.length() - c.length()) + c + (!StringUtils.isNullOrEmpty(m) ? ("/" + m) : m ) + unitName;
        }
        return Util.repeat(' ', m.length()) + m + unitName;
    }

    protected String speed(ProgressState progress) {
        String suffix = "/s";
        double elapsedSeconds = progress.getElapsedAfterStart().getSeconds();
        double elapsedInUnit = elapsedSeconds;
        if (null != speedUnit) {
            switch (speedUnit) {
                /**
                 * min
                 */
                case MINUTES:
                    suffix = "/min";
                    elapsedInUnit /= 60;
                    break;
                /**
                 * HOURS
                 */
                case HOURS:
                    suffix = "/h";
                    elapsedInUnit /= (60 * 60);
                    break;
                /**
                 * DAYS
                 */
                case DAYS:
                    suffix = "/d";
                    elapsedInUnit /= (60 * 60 * 24);
                    break;
            }
        }

        if (elapsedSeconds != 0) {
            double speed = (double) (progress.current - progress.start) / elapsedInUnit;
            double speedWithUnit = speed / unitSize;
            return progress.getProgressStyle().format(speedFormat.format(speedWithUnit)) + unitName + suffix;
        } else {
            return "?" + unitName + suffix;
        }
    }

    public String render(ProgressState progress, int maxLength) {
        if (maxLength <= 0) {
            return "";
        }

        String prefix = progress.getTaskName() + " " + percentage(progress) + " " + style.leftBracket;
        int prefixLength = getStringDisplayLength(prefix);

        if (prefixLength > maxLength) {
            prefix = trimDisplayLength(prefix, maxLength - 1);
            prefixLength = maxLength - 1;
        }

        // length of progress should be at least 1
        int maxSuffixLength = Math.max(maxLength - prefixLength - 1, 0);

        String speedString = isSpeedShown ? speed(progress) : "";
        String etaString = etaString(progress);
        etaString  = "?".equals(etaString) ? "" : etaString;
        String suffix = style.rightBracket + " " + ratio(progress) + " ("
                + Util.formatDuration(progress.getTotalElapsed())
                + (isEtaShown ? "/" + etaString  : "")
                + ") "
                + speedString + progress.extraMessage;
        int suffixLength = getStringDisplayLength(suffix);
        // trim excessive suffix
        if (suffixLength > maxSuffixLength) {
            suffix = trimDisplayLength(suffix, maxSuffixLength);
            suffixLength = maxSuffixLength;
        }

        int length = maxLength - prefixLength - suffixLength;

        StringBuilder sb = new StringBuilder(maxLength);
        sb.append(prefix);

        // case of indefinite progress bars
        if (progress.indefinite) {
            int pos = (int) (progress.current % length);
            sb.append(Util.repeat(style.space, pos));
            sb.append(style.block);
            sb.append(Util.repeat(style.space, length - pos - 1));
        }
        // case of definite progress bars
        else {
            if (progress.ansi) {
                sb.append("\u001b[38;5;27m").append(Util.repeat(style.block, progressIntegralPart(progress, length))).append("\u001b[0;m");
            } else {
                sb.append(Util.repeat(style.block, progressIntegralPart(progress, length)));
            }
            if (progress.current < progress.max) {
                int fraction = progressFractionalPart(progress, length);
                if (fraction != 0) {
                    sb.append(style.fractionSymbols.charAt(fraction));
                    sb.append(style.delimitingSequence);
                } else {
                    sb.append(style.delimitingSequence);
                    sb.append(style.rightSideFractionSymbol);
                }
                sb.append(Util.repeat(style.space, length - progressIntegralPart(progress, length) - 1));
            }
        }

        sb.append(suffix);
        return sb.toString();
    }
}
