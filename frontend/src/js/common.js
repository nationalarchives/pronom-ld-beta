import '@styles/main.scss'


const App = () => {
  console.log("common is running")
  
}
export const currentFormSection = () => {
  currentFormSectionw = 0;
  function showFormSection(formPart){
    currentFormSectionw = formPart;
    console.log(currentFormSectionw);
  }
}

App()