package sedridor.amidst.version;

public enum VersionInfo {

    unknown(null),
    V1_9_4("64bc1bba39c50da2cc8d37bc1696540b"),
    V1_9_2("9b256b5c6d59ea02cdc3063e5b29557c"),
    V1_9_1("a0608984b3d1d9329eae2a04834fc5b7"),
    V1_9("be70420233709b3d778f7627dd50de62"),
    V1_8_9("396099e23e58309d21713ab5d7c1d1a8"),
    V1_8("8663a10cecc10eaa683a927ef5371852"),
    V1_7_10("e6b7a531b95d0c172acb704d1f54d1b3"),
    V1_7_9("5f7cc7eb01cbd935a9973cb6e9c4c10e"),
    V1_7_7("d93597ed300fccafc99137ac5d91af88"),
    V1_7_6("1a9c5836a15f7b1cdb28edb3632f5f2b"),
    V1_7_4("f9ffe7e56b26d459560c48e228cc6ad4"),
    V1_7_2("7a304554fdea879a799efe6eaedd8c95"),
    V1_6_4("2e5044f5359e82245551167a237f3167"),
    V1_6_2("1d43cdba8b9752d7f557ab7d3e365964"),
    V1_6_1("3c56fb4b77b1ca37dde69b5eb896fc4d"),
    V1_5_2("6897c3287fb971c9f362eb3ab20f5ddd"),
    V1_5_1("5c1219d869b87d233de3033688ec7567"),
    V1_4_7("8e80fb01b321c6b3c7efca397a3eea35"),
    V1_4_6("48677dc4c2b98c29918722b5ab27b4fd"),
    V1_4_5("b15e2b2b6b4629f0d99a95b6b44412a0"),
    V1_4_4("7aa46c8058cba2f38e9d2ddddcc77c72"),
    V1_4_2("771175c01778ea67395bc6919a5a9dc5"),
    V1_3_2("969699f13e5bbe7f12e40ac4f32b7d9a"),
    V1_3_1("266ccbc9798afd2eadf3d6c01b4c562a"),
    V1_2_5("8e8778078a175a33603a585257f28563"),
    V1_2_4("25423eab6d8707f96cc6ad8a21a7250a"),
    V1_2_2("6189e96efaea11e5164b4a4755574324"),
    V1_1("e92302d2acdba7c97e0d8df1e10d2006"),
    V1_0("3820d222b95d0b8c520d9596a756a6e6");

    private static final VersionInfo[] copyOfValues = values();

    public final String versionId;

    VersionInfo(String versionId) {
        this.versionId = versionId;
    }

    public boolean saveEnabled() {
        return true;
    }

    public String toString() {
        return super.toString().replace("_", ".")
            .replace("V", "");
    }

    public boolean isAtLeast(VersionInfo other) {
        return (ordinal() <= other.ordinal());
    }

    public static VersionInfo forName(String name) {
        for (VersionInfo value : copyOfValues) {
            if (value.name()
                .equals(name)) return value;
        }
        return unknown;
    }
}
