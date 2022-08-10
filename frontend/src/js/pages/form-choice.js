import '@styles/main.scss'
import '@styles/form-choice.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
  // feedback message
	let feedbackBarHight = $('.feedback-inner').height();
	$('.feedback-container').css('top','calc(100vh - '+ (feedbackBarHight + 35) +'px)');
	$('.feedback-container').css('height','auto');
}

App()