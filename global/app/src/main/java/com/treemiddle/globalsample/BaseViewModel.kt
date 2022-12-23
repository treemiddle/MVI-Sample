package com.treemiddle.globalsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {
    private val initialState: State by lazy { createInitialState() }

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State>
        get() = _uiState

    private val _event = MutableSharedFlow<Event>()

    private val _effect = Channel<Effect>()
    val effect: Flow<Effect>
        get() = _effect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    fun setEvent(event: Event) = viewModelScope.launch { _event.emit(value = event) }

    abstract fun createInitialState(): State

    abstract fun handleEvents(event: Event)

    protected fun setState(reducer: State.() -> State) =
        _uiState.update { uiState.value.reducer() }

    protected fun setEffect(builder: () -> Effect) =
        viewModelScope.launch { _effect.send(element = builder()) }

    private fun subscribeToEvents() = viewModelScope.launch {
        _event.collect {
            handleEvents(event = it)
        }
    }
}


interface UiEvent

interface UiState

interface UiEffect
