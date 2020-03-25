package fpt.capstone.inqr.callbacks;

public interface CallbackData<T> {
    void onSuccess(T t);

    void onFail(String message);
}
