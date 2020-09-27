package com.example.cocworking

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocworking.Retrofit.*
import com.example.cocworking.databinding.ActivitySalaRiunioniBinding
import com.example.cocworking.databinding.CalendarDayLayoutBinding
import com.example.cocworking.models.Event
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.activity_sala_riunioni.*
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.calendar_header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors
import javax.xml.datatype.DatatypeConstants.MINUTES

class SalaRiunioniActivity : AppCompatActivity() {

    lateinit var iMyService : IMyService
    private val today = LocalDate.now()
    private var selectedDate: LocalDate = LocalDate.now()
    private var augurimamma = "auguri mamma"
    private var augurinonna = "augurinonna"
    private var augurizia = "augurizia"
    private var defaultUserId: String? = ""
    private var defaultEmail: String? = ""

    //private var eventmap = mutableMapOf<LocalDate, List<Event>>()

    var eventi: List<Event> = emptyList()

    private val now = Calendar.getInstance()

    private val cal = Calendar.getInstance()
    private var eventTime: LocalTime = LocalTime.now()

    //private var oggi = LocalDateTime.of(LocalDate.parse("2020-09-17"), eventTime)
    private var domani = LocalDateTime.of(LocalDate.parse("2020-09-18"), eventTime)

    //val evento : Event = Event(UUID.randomUUID().toString(), defaultUserId, augurinonna, oggi)

    private var eventmap = mutableMapOf<LocalDate, List<Event>>()
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    private lateinit var binding: ActivitySalaRiunioniBinding

    private val eventsAdapter = EventAdapter {
        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Dialog)
            .setMessage(R.string.event_dialog_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                if(it.userId == defaultUserId) {
                    deleteEvent(it)
                } else {
                    Toast.makeText(this@SalaRiunioniActivity, "Impossibile eliminare evento" + it.userId + defaultUserId , Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private val inputDialog by lazy {
        //val timePicker = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->  },
        //now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false)
        val editText = AppCompatEditText(this)
        val layout = FrameLayout(this).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, context)
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle(getString(R.string.event_input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString(), eventTime)
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }

    private fun saveEvent(text: String, time: LocalTime) {
            if (text.isBlank()) {
                Toast.makeText(this, R.string.empty_input_text, Toast.LENGTH_LONG).show()
            } else {
                selectedDate?.let {
                    Log.d("time", time.toString())
                    var idEvento = UUID.randomUUID().toString()
                    eventmap[it] = eventmap[it].orEmpty().plus(Event(idEvento, defaultUserId, text, LocalDateTime.of(it,time.truncatedTo(ChronoUnit.MINUTES))))
                    updateEvents(idEvento, defaultUserId, text, LocalDateTime.of(it,time.truncatedTo(ChronoUnit.MINUTES)))
                    updateAdapterForDate(it)
                }
            }
        }


        private fun deleteEvent(event: Event) {
            val date = event.date
            eventmap[date.toLocalDate()] = eventmap[date.toLocalDate()].orEmpty().minus(event)
            Log.d("mappa eventi", eventmap.toString())
            Log.d("evento incriminato", event.eventId)
            Log.d("evento incriminato", event.userId)
            Log.d("evento incriminato", event.text)
            Log.d("evento incriminato", event.date.toString())
            eventmap[date.toLocalDate()] = eventmap.getValue(date.toLocalDate()).orEmpty().minus(event)
            deleteEvents(event.eventId)
            updateAdapterForDate(date.toLocalDate())
        }

        private fun updateAdapterForDate(date: LocalDate) {
            eventsAdapter.apply {
                events.clear()
                this@SalaRiunioniActivity.eventmap[date]?.stream()
                    ?.distinct()
                    ?.collect(Collectors.toList())
                Log.d(this@SalaRiunioniActivity.eventmap[date]?.size.toString(), "dimensione lista")
                events.addAll(this@SalaRiunioniActivity.eventmap[date].orEmpty())
                notifyDataSetChanged()
            }
            selectedDateText?.text = selectionFormatter.format(date)
        }

        private fun selectDate(date: LocalDate) {
            if (selectedDate != date) {
                val oldDate = selectedDate
                selectedDate = date
                oldDate?.let { calendarView?.notifyDateChanged(it) }
                calendarView?.notifyDateChanged(date)
                calendarView?.notifyDateChanged(oldDate)
                updateAdapterForDate(date)
            }
    }

    private fun initEventRecyclerView(){
        eventsRv?.apply{
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sala_riunioni)
        setSupportActionBar(findViewById(R.id.toolbar_orange))

        val mypreference = MyPreference(this)
        defaultUserId = mypreference.getAccountInfo()
        defaultEmail = mypreference.getPreferenceEmail()

        val binding = ActivitySalaRiunioniBinding.inflate(layoutInflater)

        initEventRecyclerView()

        val retrofit = RetrofitClient.getInstance() //salvo nella variabile retrofit l'istanza ritornata dalla funzione getInsance dell'oggetto RetrofitClient
        iMyService = retrofit.create(IMyService::class.java)

        if (savedInstanceState == null) {
            calendarView?.post {
                // Show today's events initially.
                selectDate(today)
            }
        }


        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarDayText
            val dotView = view.DotView
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.inflate(layoutInflater)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                    selectDate(day.date)
                    }
                }
            }
        }

        takeEvents("ada")

        calendarView?.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.colorPrimary)
                            textView.setBackgroundResource(R.drawable.today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.colorBase2)
                            textView.setBackgroundResource(R.drawable.selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.colorPrimaryDark)
                            textView.background = null
                            if(eventmap[day.date].orEmpty().isNotEmpty()) {
                                dotView.makeVisible()
                            }
                            else{dotView.makeInVisible()
                            }
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarHeaderText
        }
        calendarView?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                container.textView.text = monthTitle
            }
        }


        //eventi.toMutableList().add(evento)
        //eventi = eventi.orEmpty().plusElement(Event(UUID.randomUUID().toString(), defaultUserId, augurimamma, oggi))
        //eventi.plusElement(Event(UUID.randomUUID().toString(), defaultUserId, augurizia, domani))

        /*Log.d(eventi.size.toString(), "dimensione lista eventi")

        eventmap = eventi.groupBy { it.date.toLocalDate() }.toMutableMap()
        Log.d(eventmap.size.toString(), "dimensione mappa")*/

        AddButton?.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE, minute)
                eventTime = LocalDateTime.ofInstant(cal.toInstant(), cal.timeZone.toZoneId()).toLocalTime()
                var chosenDate = LocalDateTime.of(selectedDate,eventTime.truncatedTo(ChronoUnit.MINUTES))
                Log.d("chosen date", chosenDate.toString())
                checkFreeDate(chosenDate)

            }
            TimePickerDialog(this, R.style.Theme_MaterialComponents_Dialog,timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView?.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView?.scrollToMonth(currentMonth)
        Log.d("CALENDAR_CREATION","$currentMonth \n $firstMonth \n $lastMonth \n $firstDayOfWeek")
    }

    private fun updateEvents(eventId: String, userId: String?, text: String, date:LocalDateTime) {

        iMyService.updateEvents(eventId , userId, text, date).enqueue(object :
            Callback<String> { //enqueue è un metodo che serve per lanciare la Call
            override fun onFailure(call: Call<String>, t: Throwable) {
                //Log.d("ricevo questo", Array<Any>().toString())
                Toast.makeText(this@SalaRiunioniActivity, "Error" , Toast.LENGTH_SHORT).show() //mostra un messaggio nel contesto della LoginActivity
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                //Log.d("ricevo questo", response.body()?.get(1).toString())
                Toast.makeText(this@SalaRiunioniActivity, "" + response.body(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun checkFreeDate(date:LocalDateTime) {

        Log.d("passed date", date.toString())

        iMyService.checkFreeDate(date).enqueue(object :
            Callback<DataChecked> { //enqueue è un metodo che serve per lanciare la Call
            override fun onFailure(call: Call<DataChecked>, t: Throwable) {
                //Log.d("ricevo questo", Array<Any>().toString())
                Toast.makeText(this@SalaRiunioniActivity, "Error" , Toast.LENGTH_SHORT).show() //mostra un messaggio nel contesto della LoginActivity
            }

            override fun onResponse(call: Call<DataChecked>, response: Response<DataChecked>) {
                //Log.d("ricevo questo", response.body()?.get(1).toString())
                Toast.makeText(this@SalaRiunioniActivity, "" + response.body()?.message, Toast.LENGTH_SHORT).show()
                if(response.body()?.flag == 1 ){
                    inputDialog.show()
                }
            }

        })

    }

    private fun takeEvents(userId: String) {

        RetrofitClient2.instance.takeEvents(userId).enqueue(object :
            Callback<List<EventsUpdated>> { //enqueue è un metodo che serve per lanciare la Call
            override fun onFailure(call: Call<List<EventsUpdated>>, t: Throwable) {
                Log.d("errore", t.message)
                //Log.d("ricevo questo", Array<Any>().toString())
                Toast.makeText(this@SalaRiunioniActivity, "Error", Toast.LENGTH_SHORT)
                    .show() //mostra un messaggio nel contesto della LoginActivity
            }

            override fun onResponse(call: Call<List<EventsUpdated>>, response: Response<List<EventsUpdated>>) {
                val updated = response.body()
               // updated?.forEach( {e => eventi.orEmpty().plusElement(Event(e.eventId, e.userId, e.text, e.date.toLocalDateTime))})
                /*val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                updated?.forEach{eventi += Event(it.eventId, it.userId, it.text, LocalDateTime.parse(it.date, formatter))}*/
                updated?.forEach{eventi += Event(it.eventId, it.userId, it.text,  LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")))}
                Log.d(eventi.size.toString(), "dimensione lista eventi")
                eventi.forEach{calendarView?.notifyDateChanged(it.date.toLocalDate())}
                eventmap = eventi.groupBy { it.date.toLocalDate() }.toMutableMap()
                Log.d(eventmap.size.toString(), "dimensione mappa")
                Log.d("ricevo questo", response.body().toString())
                //Log.d("ricevo questo", response.body()?.get(1).toString())
                //Log.d("ricevo questo", eventi?.get(1)?.toString())

                //response.body()?.forEach { e ->  eventi.toMutableList().add(Event(e[0], e.userId, e.text, e.date))}
                /*Toast.makeText(this@SalaRiunioniActivity, "" + response.body(), Toast.LENGTH_SHORT)
                    .show()*/
            }

        })
    }

    private fun deleteEvents(eventId: String) {

        Log.d("messaggio","sono entrato")

        iMyService.deleteEvents(eventId).enqueue(object :
            Callback<String> { //enqueue è un metodo che serve per lanciare la Call
            override fun onFailure(call: Call<String>, t: Throwable) {
                //Log.d("ricevo questo", Array<Any>().toString())
                Toast.makeText(this@SalaRiunioniActivity, "Error" , Toast.LENGTH_SHORT).show() //mostra un messaggio nel contesto della LoginActivity
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                //Log.d("ricevo questo", response.body()?.get(1).toString())
                Toast.makeText(this@SalaRiunioniActivity, "" + response.body(), Toast.LENGTH_SHORT).show()
            }

        })

    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //Funzioni che si attivano quando viene cliccato un elemento della barra
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.setting -> {
            // User chose the "Settings" item, show the app settings UI...
            val setting = Intent(applicationContext,SetupActivity::class.java)
            startActivity(setting)
            true
        }
        R.id.account -> {
            //Se clicco su "profilo" si apre l'activity relativa
            val profilo = Intent(applicationContext,ProfiloActivity::class.java)
            startActivity(profilo)
            true
        }
        R.id.allarm -> {
            // User chose the "Allarm" action for see notifications
            val notifiche = Intent(applicationContext,NotificheActivity::class.java)
            startActivity(notifiche)
            true
        }
        R.id.home -> {
            // uso "home" action per aprire MainActivity
            val home = Intent(applicationContext,MainActivity::class.java)
            startActivity(home)
            true
        }
        R.id.logout -> {
            // uso "home" action per aprire MainActivity
            val mypreference = MyPreference(this)
            mypreference.deleteAccountInfo(defaultUserId, defaultEmail)
            val login = Intent(applicationContext,LoginActivity::class.java)
            startActivity(login)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
