package com.bonree.android.data.parse.module;

public class ParamsData {
    private String expect_spdy;
    private String original_url;
    private String priority;
    private SourceData source_dependency;//关联的父ID
    private String using_quic;
    private String method;

    private boolean has_upload;
    private boolean is_pending;
    private int load_flags;
    private int load_state;
    private int net_error = 0;
    private String status;
    private String headers;
    private String url;
    private int byte_count = 0;

    private int address_family;
    private boolean allow_cached_response;
    private String host;
    private boolean is_speculative;
    private String address;

    private String address_list;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddress_family() {
        return address_family;
    }

    public void setAddress_family(int address_family) {
        this.address_family = address_family;
    }

    public boolean isAllow_cached_response() {
        return allow_cached_response;
    }

    public void setAllow_cached_response(boolean allow_cached_response) {
        this.allow_cached_response = allow_cached_response;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isIs_speculative() {
        return is_speculative;
    }

    public void setIs_speculative(boolean is_speculative) {
        this.is_speculative = is_speculative;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getAddress_list() {
        return address_list;
    }

    public void setAddress_list(String address_list) {
        this.address_list = address_list;
    }

    public int getByte_count() {
        return byte_count;
    }

    public void setByte_count(int byte_count) {
        this.byte_count = byte_count;
    }

    private String parsmsStr;

    public String getParsmsStr() {
        return parsmsStr;
    }

    public void setParsmsStr(String parsmsStr) {
        this.parsmsStr = parsmsStr;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getExpect_spdy() {
        return expect_spdy;
    }

    public void setExpect_spdy(String expect_spdy) {
        this.expect_spdy = expect_spdy;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public SourceData getSource_dependency() {
        return source_dependency;
    }

    public void setSource_dependency(SourceData source_dependency) {
        this.source_dependency = source_dependency;
    }

    public String getUsing_quic() {
        return using_quic;
    }

    public void setUsing_quic(String using_quic) {
        this.using_quic = using_quic;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isHas_upload() {
        return has_upload;
    }

    public void setHas_upload(boolean has_upload) {
        this.has_upload = has_upload;
    }

    public boolean isIs_pending() {
        return is_pending;
    }

    public void setIs_pending(boolean is_pending) {
        this.is_pending = is_pending;
    }

    public int getLoad_flags() {
        return load_flags;
    }

    public void setLoad_flags(int load_flags) {
        this.load_flags = load_flags;
    }

    public int getLoad_state() {
        return load_state;
    }

    public void setLoad_state(int load_state) {
        this.load_state = load_state;
    }

    public int getNet_error() {
        return net_error;
    }

    public void setNet_error(int net_error) {
        this.net_error = net_error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
